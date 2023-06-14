package ru.itis.zheleznov.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.zheleznov.entities.LibraryNode;
import ru.itis.zheleznov.entities.PublisherInformation;
import ru.itis.zheleznov.services.LibraryMetadataParser;
import ru.itis.zheleznov.services.UrlActivityService;
import ru.itis.zheleznov.utils.ReductionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegexpLibraryMetadataParser implements LibraryMetadataParser {

    private static final String AND_ANOTHER = "[и др.].";
    private static final String AND_ANOTHER_REGEXP = "\\[и др.].";

    private final UrlActivityService simpleUrlActivityService;

    public RegexpLibraryMetadataParser(UrlActivityService simpleUrlActivityService) {
        this.simpleUrlActivityService = simpleUrlActivityService;
    }

    @Override
    public LibraryNode parseMetadata(String libraryRawNode) {
        String textCopy = libraryRawNode.replaceAll("\\n", " ").replaceAll(" - ", " – ").replaceAll(" — ", " – ");
        textCopy = textCopy.replaceAll(AND_ANOTHER_REGEXP, "," + AND_ANOTHER);

        List<String> heading = new ArrayList<>();
        String title = "";
        List<String> informationAboutResponsibility = new ArrayList<>();
        String additionalInformationAboutResponsibility = "";
        String mainResourceType = "";
        String mainResourceTitle = "";
        String additionalInformationAboutMainResourceTitle = "";
        String informationAboutThePublication = "";
        Set<PublisherInformation> publisherInformation = new HashSet<>();
        String publicationYear = "";
        String publicationNumber = "";
        String volume = "";
        String note = "";
        String isbn = "";
        String url = "";
        Boolean isUrlActive = null;
        List<String> accessModes = new ArrayList<>();
        String typeOfContent = "";
        List<String> informationRelatedToTheTitle = new ArrayList<>();
        try {
            String headingRegexp = "^[А-Яа-яA-Za-z]+,?\\s([А-Яа-яA-Za-z][.]\\s?){1,2}";
            Pattern headingPattern = Pattern.compile(headingRegexp);
            Matcher headingMatcher = headingPattern.matcher(textCopy);
            String textWithoutHeading = textCopy;
            while (headingMatcher.find()) {
                heading.add(textCopy.substring(headingMatcher.start(), headingMatcher.end()).trim());
                textWithoutHeading = textCopy.substring(headingMatcher.end());
            }
            String titleDelimiter = ":";
            if (textWithoutHeading.contains(":")) {
                if (textWithoutHeading.indexOf(":") > textWithoutHeading.indexOf("/")) {
                    titleDelimiter = "/";
                } else if (textWithoutHeading.contains("//") && textWithoutHeading.indexOf(":") > textWithoutHeading.indexOf("//")) {
                    titleDelimiter = "//";
                }
            } else if (!textWithoutHeading.contains(":")) {
                if (textWithoutHeading.contains("//") && textWithoutHeading.indexOf("/") > textWithoutHeading.indexOf("//")) {
                    titleDelimiter = "//";
                } else if (textWithoutHeading.contains("//") && textWithoutHeading.indexOf("/") < textWithoutHeading.indexOf("//")) {
                    titleDelimiter = "/";
                }
            }
            title = textWithoutHeading.split(titleDelimiter)[0].trim();
            String textWithoutTitle = textWithoutHeading.substring(title.length()).trim();
            if (textWithoutTitle.charAt(0) == ':') {
                String informationRelatedToTheTitleRaw = textWithoutTitle.substring(1, textWithoutTitle.indexOf("/")).trim();
                informationRelatedToTheTitle.addAll(Arrays.stream(informationRelatedToTheTitleRaw.split(":")).map(String::trim).toList());
            }

            int informationRelatedToTheTitleLength = 0;
            if (!informationRelatedToTheTitle.isEmpty()) {
                informationRelatedToTheTitleLength = getListLength(informationRelatedToTheTitle);
            }
            String textWithoutInformationRelatedToTheTitle = textWithoutTitle.substring(informationRelatedToTheTitleLength).trim();
            if (textWithoutInformationRelatedToTheTitle.startsWith("/ ")) {
                if (textWithoutInformationRelatedToTheTitle.contains(";") && textWithoutInformationRelatedToTheTitle.indexOf(";") < textWithoutInformationRelatedToTheTitle.indexOf("–")) {
                    String informationAboutResponsibilityRaw = textWithoutInformationRelatedToTheTitle.substring(1, textWithoutInformationRelatedToTheTitle.indexOf(";")).trim();
                    informationAboutResponsibility = Arrays.stream(informationAboutResponsibilityRaw.split(",")).filter(resp -> !resp.isBlank()).toList();
                    additionalInformationAboutResponsibility = textWithoutInformationRelatedToTheTitle.substring(informationAboutResponsibilityRaw.length() + 4, textWithoutInformationRelatedToTheTitle.indexOf("–")).trim();
                } else {
                    if (Pattern.matches("[А-ЯA-Z]", textWithoutInformationRelatedToTheTitle.charAt(2) + "")) {
                        String responsibilityDelimiter = "–";
                        if (textWithoutInformationRelatedToTheTitle.contains("//")) {
                            responsibilityDelimiter = "//";
                        }
                        String informationAboutResponsibilityRaw = textWithoutInformationRelatedToTheTitle.substring(1, textWithoutInformationRelatedToTheTitle.indexOf(responsibilityDelimiter)).trim();
                        informationAboutResponsibility = Arrays.stream(informationAboutResponsibilityRaw.split(",")).filter(resp -> !resp.isBlank()).toList();
                    } else {
                        additionalInformationAboutResponsibility = textWithoutInformationRelatedToTheTitle.substring(1, textWithoutInformationRelatedToTheTitle.indexOf("–")).trim();
                    }
                }
            }

            if (textWithoutInformationRelatedToTheTitle.contains("// ")) {
                String textAboutMainResourceAndOther = "";
                if (textWithoutInformationRelatedToTheTitle.contains("–") && textWithoutInformationRelatedToTheTitle.indexOf("–") < textWithoutInformationRelatedToTheTitle.indexOf(" // ")) {
                    textAboutMainResourceAndOther = textWithoutInformationRelatedToTheTitle.substring(textWithoutInformationRelatedToTheTitle.indexOf("–") + 1, textWithoutInformationRelatedToTheTitle.indexOf(" // ")).trim();
                    mainResourceType = textAboutMainResourceAndOther.split(":")[1].trim();
                }

                String mainResourseTitleDelimiter = "–";
                textAboutMainResourceAndOther = textWithoutInformationRelatedToTheTitle.substring(textWithoutInformationRelatedToTheTitle.indexOf(" // ") + 3).trim();
                if (textAboutMainResourceAndOther.contains(":") && textAboutMainResourceAndOther.indexOf(":") < textAboutMainResourceAndOther.indexOf("–")) {
                    mainResourseTitleDelimiter = ":";
                }
                mainResourceTitle = textAboutMainResourceAndOther.substring(0, textAboutMainResourceAndOther.indexOf(mainResourseTitleDelimiter)).trim();
                if (textAboutMainResourceAndOther.contains(":") && textAboutMainResourceAndOther.indexOf(":") < textAboutMainResourceAndOther.indexOf("–")) {
                    additionalInformationAboutMainResourceTitle = textAboutMainResourceAndOther.substring(textAboutMainResourceAndOther.indexOf(":") + 1, textAboutMainResourceAndOther.indexOf("–")).trim();
                }
                String textPublisherAndOther = textAboutMainResourceAndOther.substring(textAboutMainResourceAndOther.indexOf("–") + 1).trim();

                if (textPublisherAndOther.contains(",") && textPublisherAndOther.indexOf(",") < textPublisherAndOther.indexOf(" – ")) {
                    textPublisherAndOther = textPublisherAndOther.substring(0, textPublisherAndOther.indexOf(",")).trim();
                    for (String complexInformationAboutThePublication : textPublisherAndOther.split(";")) {
                        String[] splitInformationAboutThePublication = complexInformationAboutThePublication.split(":");
                        publisherInformation.add(new PublisherInformation(splitInformationAboutThePublication[0].trim(), splitInformationAboutThePublication[1].trim()));
                    }
                    String textPublisherAndOtherTemp = textAboutMainResourceAndOther.substring(textAboutMainResourceAndOther.indexOf("–") + 1).trim();
                    String textWithoutPublisher = textPublisherAndOtherTemp.trim().substring(textPublisherAndOtherTemp.indexOf(",") + 2).trim();
                    List<String> publishingInfo = Arrays.stream(textWithoutPublisher.split(" – ")).toList();
                    if (textWithoutPublisher.contains(",")) {
                        publishingInfo = Arrays.stream(textWithoutPublisher.substring(0, textWithoutPublisher.indexOf(",")).split(" – ")).toList();
                    }
                    if (!publishingInfo.isEmpty()) {
                        if (publishingInfo.size() >= 2) {
                            publicationYear = publishingInfo.get(0);
                            volume = publishingInfo.get(1);
                            if (publishingInfo.size() >= 3) {
                                if (publishingInfo.get(1).contains("№")) {
                                    publicationNumber = publishingInfo.get(1);
                                    volume = publishingInfo.get(2);
                                } else if (publishingInfo.get(2).contains("URL")) {
                                    volume = publishingInfo.get(1);
                                    url = publishingInfo.get(2).split(": ")[1];
                                }
                                if (publishingInfo.size() == 4) {
                                    if (publishingInfo.get(3).contains("URL")) {
                                        url = publishingInfo.get(3).split(": ")[1];
                                    }
                                }
                            }
                        }
                    }
                } else {
                    List<String> publishingInfo = Arrays.stream(textAboutMainResourceAndOther.substring(textAboutMainResourceAndOther.indexOf(" – ") + 2).trim().split(" – ")).toList();
                    if (textPublisherAndOther.contains(",")) {
                        publishingInfo = Arrays.stream(textPublisherAndOther.substring(0, textPublisherAndOther.indexOf(",")).trim().split(" – ")).toList();
                    }
                    if (!publishingInfo.isEmpty()) {
                        if (publishingInfo.size() >= 2) {
                            publicationYear = publishingInfo.get(0);
                            volume = publishingInfo.get(1);
                            if (publishingInfo.size() >= 3) {
                                if (publishingInfo.get(1).contains("№")) {
                                    publicationNumber = publishingInfo.get(1);
                                    volume = publishingInfo.get(2);
                                } else if (publishingInfo.get(2).contains("URL")) {
                                    volume = publishingInfo.get(1);
                                    url = publishingInfo.get(2).split(": ")[1];
                                }
                                if (publishingInfo.size() == 4) {
                                    if (publishingInfo.get(3).contains("URL")) {
                                        url = publishingInfo.get(3).split(": ")[1];
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                String textWithoutInformationAboutResponsibility = textWithoutInformationRelatedToTheTitle.substring(textWithoutInformationRelatedToTheTitle.indexOf("–") + 1).trim();
                if (textWithoutInformationAboutResponsibility.contains("–") && textWithoutInformationAboutResponsibility.indexOf("–") < textWithoutInformationAboutResponsibility.indexOf(":")) {
                    informationAboutThePublication = textWithoutInformationAboutResponsibility.split("–")[0].trim();
                }

                String textWithoutInformationAboutThePublication = textWithoutInformationAboutResponsibility;
                if (!informationAboutThePublication.isEmpty()) {
                    textWithoutInformationAboutThePublication = textWithoutInformationAboutResponsibility.replaceAll(informationAboutThePublication, "").substring(2).trim();
                }
                String textWithoutInformationAboutThePublicationAndOther = textWithoutInformationAboutThePublication.substring(0, textWithoutInformationAboutThePublication.indexOf(",")).trim();
                for (String complexInformationAboutThePublication : textWithoutInformationAboutThePublicationAndOther.split(";")) {
                    String[] splitInformationAboutThePublication = complexInformationAboutThePublication.split(":");
                    publisherInformation.add(new PublisherInformation(splitInformationAboutThePublication[0].trim(), splitInformationAboutThePublication[1].trim()));
                }

                String textWithoutPublisherInformation = textWithoutInformationAboutThePublication.substring(textWithoutInformationAboutThePublication.indexOf(",") + 1).trim();
                publicationYear = textWithoutPublisherInformation.substring(0, textWithoutPublisherInformation.indexOf(".")).trim();

                String[] splitTextWithoutPublisherInformation = textWithoutPublisherInformation.split("–");
                volume = splitTextWithoutPublisherInformation[1].trim();

                if (splitTextWithoutPublisherInformation.length > 2) {
                    if (
                            !splitTextWithoutPublisherInformation[2].contains("ISBN") && !splitTextWithoutPublisherInformation[2].toLowerCase().contains("текст")
                                    && !splitTextWithoutPublisherInformation[2].contains("URL") && !splitTextWithoutPublisherInformation[2].toLowerCase().contains("режим доступа")
                    ) {
                        note = splitTextWithoutPublisherInformation[2].trim();
                        if (splitTextWithoutPublisherInformation.length > 3 && splitTextWithoutPublisherInformation[3].contains("ISBN")) {
                            isbn = splitTextWithoutPublisherInformation[3].trim();
                            if (splitTextWithoutPublisherInformation.length > 4 && splitTextWithoutPublisherInformation[4].contains("URL")) {
                                url = splitTextWithoutPublisherInformation[4].trim().replaceAll("URL:", "").trim();
                            }
                            if (splitTextWithoutPublisherInformation.length > 5 && splitTextWithoutPublisherInformation[5].toLowerCase().contains("режим доступа")) {
                                String[] accessModesRaw = splitTextWithoutPublisherInformation[5].trim().split(":");
                                accessModes = Arrays.stream(accessModesRaw[1].split(";")).toList();
                            }
                        } else if (splitTextWithoutPublisherInformation.length > 3 && splitTextWithoutPublisherInformation[3].contains("URL")) {
                            url = splitTextWithoutPublisherInformation[3].trim().replaceAll("URL:", "").trim();
                            if (splitTextWithoutPublisherInformation.length > 4 && splitTextWithoutPublisherInformation[4].toLowerCase().contains("режим доступа")) {
                                String[] accessModesRaw = splitTextWithoutPublisherInformation[4].trim().split(":");
                                accessModes = Arrays.stream(accessModesRaw[1].split(";")).toList();
                            }
                        }
                    } else if (splitTextWithoutPublisherInformation[2].contains("ISBN")) {
                        isbn = splitTextWithoutPublisherInformation[2].trim();
                        if (splitTextWithoutPublisherInformation.length > 3 && splitTextWithoutPublisherInformation[3].contains("URL")) {
                            url = splitTextWithoutPublisherInformation[3].trim().replaceAll("URL:", "").trim();
                        }
                        if (splitTextWithoutPublisherInformation.length > 4 && splitTextWithoutPublisherInformation[4].toLowerCase().contains("режим доступа")) {
                            String[] accessModesRaw = splitTextWithoutPublisherInformation[4].trim().split(":");
                            accessModes = Arrays.stream(accessModesRaw[1].split(";")).toList();
                        }
                    }
                }

                int typeOfContentOrder = 2 + (note.isEmpty() ? 0 : 1) + (isbn.isEmpty() ? 0 : 1) + (url.isEmpty() ? 0 : 1) + (accessModes.isEmpty() ? 0 : 1);
                if (splitTextWithoutPublisherInformation.length > typeOfContentOrder && splitTextWithoutPublisherInformation[typeOfContentOrder].toLowerCase().contains("текст")) {
                    typeOfContent = splitTextWithoutPublisherInformation[typeOfContentOrder].trim().split(":")[1];
                }
            }
            if (!url.isBlank()) {
                url = url.replaceAll("http:", "https:");
                isUrlActive = simpleUrlActivityService.checkUrlActive(url);
            }

            LibraryNode libraryNode = new LibraryNode(libraryRawNode, heading.toString(), title, informationRelatedToTheTitle.toString(), informationAboutResponsibility.toString(), additionalInformationAboutResponsibility, mainResourceType, mainResourceTitle, additionalInformationAboutMainResourceTitle, informationAboutThePublication, publisherInformation, publicationYear, publicationNumber, volume, note, isbn, url, isUrlActive, accessModes.toString(), typeOfContent);
            return removeReductions(libraryNode);
        } catch (Exception e) {
            log.error("Error when parsing library, e: {}", e.getMessage());
            if (!url.isBlank()) {
                url = url.replaceAll("http:", "https:");
                isUrlActive = simpleUrlActivityService.checkUrlActive(url);
            }

            LibraryNode libraryNode = new LibraryNode(libraryRawNode, heading.toString(), title, informationRelatedToTheTitle.toString(), informationAboutResponsibility.toString(), additionalInformationAboutResponsibility, mainResourceType, mainResourceTitle, additionalInformationAboutMainResourceTitle, informationAboutThePublication, publisherInformation, publicationYear, publicationNumber, volume, note, isbn, url, isUrlActive, accessModes.toString(), typeOfContent, false);
            return removeReductions(libraryNode);
        }
    }

    private static LibraryNode removeReductions(LibraryNode libraryNode) {
        for (Map.Entry<String, String> reduction : ReductionUtils.reductions.entrySet()) {
            libraryNode.setTitle(libraryNode.getTitle().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setVolume(libraryNode.getVolume().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setInformationRelatedToTheTitle(libraryNode.getInformationRelatedToTheTitle().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setInformationAboutResponsibility(libraryNode.getInformationAboutResponsibility().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setAdditionalInformationAboutResponsibility(libraryNode.getAdditionalInformationAboutResponsibility().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setInformationAboutThePublication(libraryNode.getInformationAboutThePublication().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setPublisherInformation(libraryNode.getPublisherInformation().stream().peek(pi -> {
                pi.setPublisherCity(pi.getPublisherName().replaceAll(reduction.getKey(), reduction.getValue()));
                pi.setPublisherName(pi.getPublisherCity().replaceAll(reduction.getKey(), reduction.getValue()));
            }).collect(Collectors.toSet()));
            libraryNode.setNote(libraryNode.getNote().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setAccessMode(libraryNode.getAccessMode().replaceAll(reduction.getKey(), reduction.getValue()));
        }
        return libraryNode;
    }

    private static int getListLength(List<String> informationRelatedToTheTitle) {
        int informationRelatedToTheTitleLength;
        informationRelatedToTheTitleLength = informationRelatedToTheTitle.stream().mapToInt(String::length).sum() + informationRelatedToTheTitle.size() + informationRelatedToTheTitle.size() + 1 + informationRelatedToTheTitle.size() - 1;
        return informationRelatedToTheTitleLength;
    }
}
