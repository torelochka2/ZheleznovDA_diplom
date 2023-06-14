package ru.itis.zheleznov.runners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.itis.zheleznov.entities.Library;
import ru.itis.zheleznov.entities.LibraryNode;
import ru.itis.zheleznov.entities.PublisherInformation;
import ru.itis.zheleznov.repositories.LibraryNodeRepository;
import ru.itis.zheleznov.repositories.LibraryRepository;
import ru.itis.zheleznov.repositories.PublisherInformationRepository;
import ru.itis.zheleznov.services.MetaDataAdditionService;
import ru.itis.zheleznov.utils.ReductionUtils;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TestRunner implements CommandLineRunner {

    private static final String AND_ANOTHER = "[и др.].";
    private static final String AND_ANOTHER_REGEXP = "\\[и др.].";

    private final ObjectMapper objectMapper;
    private final PublisherInformationRepository publisherInformationRepository;
    private final LibraryNodeRepository libraryNodeRepository;
    private final LibraryRepository libraryRepository;
    private final MetaDataAdditionService scholarParserMetaDataAdditionService;

    public TestRunner(ObjectMapper objectMapper, PublisherInformationRepository publisherInformationRepository, LibraryNodeRepository libraryNodeRepository, LibraryRepository libraryRepository, MetaDataAdditionService scholarParserMetaDataAdditionService) {
        this.objectMapper = objectMapper;
        this.publisherInformationRepository = publisherInformationRepository;
        this.libraryNodeRepository = libraryNodeRepository;
        this.libraryRepository = libraryRepository;
        this.scholarParserMetaDataAdditionService = scholarParserMetaDataAdditionService;
    }

    @Override
    public void run(String... args) throws Exception {
        String text0 = """
                Французский язык в сфере юриспруденции / И. С. Голованова, Ю. Д. Ермакова, Л. В. Капустина [и др.]. - Самара :
                Изд-во Самар. гос. экон. ун-та, 2019. - 54 с. - ISBN 978-5-906432-21-6. - Текст :
                непосредственный.
                """;
        String text1 = """
                Дорман, В. Н. Экономика организации. Ресурсы коммерческой
                организации : учебное пособие / В. Н. Дорман ; под редакцией Н. Р.
                Кельчевской. - Москва : Юрайт ; Екатеринбург : Изд-во Урал. ун-та, 2019. - 134
                с. - (Профессиональное образование). – ISBN 5-09-003516-4. -  Текст :
                непосредственный.
                """;
        String text2 = """
                Троицкий, Н. А. Лекции по русской истории ХIХ века : краткий курс :
                учеб. пособие для сред. шк., гимназий, лицеев, вузов / Н. А. Троицкий. –
                Саратов : Слово, 1994. – 272 с. – ISBN 5-85571-082-3.
                """;
        String text3 = """
                История средних веков : учеб.-метод. пособие для студентов-заочников 2
                курса пед. ин-тов / М. Л. Абрамсон, К. Д. Авдеева, В. М. Алексеев, Л. Н.
                Лебедева ; под ред. М. Л. Абрамсон, К. Д. Авдеевой. – 2-е изд., перераб. и
                доп. – Москва : Просвещение, 1991. – 191 с.
                """;
        String text4 = """
                История сервиса : учебное пособие / В. Э. Багдасарян, И. Б. Орлов, М. В.
                Катагошина, С. А. Коротков. - 2-е изд. перераб. и доп. - Москва : ИНФРА-М,
                2018. - 337 с. - (Высшее образование. Бакалавриат). - ISBN 978-5-16-012845-0. -
                Текст : непосредственный.
                """;
        String text5 = """
                Агапов, А. Б. Административное право : в 2 т. Т. 1. Общая часть : учебник
                для бакалавриата и магистратуры / А. Б. Агапов. - 11-е изд., перераб. и доп. -
                Москва : Юрайт, 2019. - 471 с. - (Бакалавр и магистр. Академический курс). -
                ISBN 978-5-534-09985-0. - URL: https://biblio-online.ru/bcode/429093 (дата
                обращения: 05.08.2019). - Режим доступа: Электронно-библиотечная система
                Юрайт. - Текст : электронный.
                """;
        String text6 = """
                Российские регионы в условиях санкций : возможности опережающие
                развития экономики на основе инноваций : монография / под общ. ред. Г. А.
                Хмелевой. - Самара : Изд-во Самар. гос. экон. ун-та, 2019. - 446 с. - ISBN 978-5-
                94622-873-2. - URL: http://lib1.sseu.ru/MegaPro (дата обращения: 09.08.2019). -
                Режим доступа: Автоматизированная интегрированная библиотечная система
                «МегаПро» ; для зарегистрир. пользователей СГЭУ. - Текст : электронный.
                """;
        String text7 = """
                Управление промышленностью в России: экономика, экология и
                общество : монография / А. А. Гибадуллин, В. Н. Пуляева, Е. Н. Харитонова, Н.
                А. Харитонова ; Государственный университет управления. - Москва :
                Издательский дом ГУУ, 2019. - 184 с. - ISBN978-5-215-03192-6. - URL:
                https://elibrary.ru/download/elibrary_37535400_17655770.PDF (дата обращения:
                27.06.2019). - Режим доступа: Научная электронная библиотека eLIBRARY.RU.
                - Текст : электронный.
                """;

        String text9 = """
                Малые научные предприятия как фактор конкуренции университетов / Ключкарев Г. А., Чурсина А. В. – DOI 10.19181/vis.2017.22.3.471. –
                Текст : электронный
                """;

        String text10 = """
                Петр Великий и Библиотека Академии наук / В. П. Леонов. – Текст :
                непосредственный // Библиотековедение. – 2010. – № 6. – С. 64–69.
                """;

        String text11 = """
                Петр Великий и Библиотека Академии наук / В. П. Леонов. // Библиотековедение. – 2010. – № 6. – С. 64–69.
                """;

        String text12 = """
                Петр Великий и Библиотека Академии наук / В. П. Леонов. – Текст :
                непосредственный // Библиотековедение. : комментированное издание – 2010. – № 6. – С. 64–69.
                """;

        String text13 = """
                Петр Великий и Библиотека Академии наук / В. П. Леонов. – Текст :
                непосредственный // Библиотековедение. : комментированное издание – Москва : Русское слово, – 2010. – № 6. – С. 64–69. – URL:
                https://elibrary.ru/download/elibrary_37535400_17655770.PDF
                """;

        String text14 = """
                Гафурова П. О. Методы нормализации метаданных электронных математических коллекций / П.О. Гафурова, А.М.Елизаров, Е.К.Липачёв // Ученые записки ИСГЗ. - 2019. - №1 (17). - С.141-148.\s
                F_uz_1_19_p141_148.pdf
                """;

        String text15 = """
                Аннушкина, В. В. Исторические предпосылки формирования
                первоначального накопления капитала / В. В. Аннушкина. - Текст :
                непосредственный // История экономических учений : учебное пособие / В. В.
                Аннушкина. - Саратов : Орион, 2018. - С. 18-29.
                """;

        String text16 = """
                Елизаров А.М. Метод автоматизированного подбора рецензентов научных статей, реализованный в информационной системе научного журнала/ А.М. Елизаров., Е.К. Липачев, Ш.М. Хайдаров // Научный сервис в сети Интернет: труды XXI Всероссийской научной конференции (23-28 сентября 2019 г., г. Новороссийск). — М.: ИПМ им. М.В.Келдыша, 2019. — С. 318-328. — URL: http://keldysh.ru/abrau/2019/theses/94.pdf, doi:10.20948/abrau-2019-94.                
                """;

        String text17 = """
                Елизаров А.М. Метод автоматизированного подбора рецензентов научных статей, реализованный в информационной системе научного журнала/ А.М. Елизаров., Е.К. Липачев, Ш.М. Хайдаров // Научный сервис в сети Интернет: труды XXI Всероссийской научной конференции (23-28 сентября 2019 г., г. Новороссийск). — 2019. — С. 318-328. — URL: http://keldysh.ru/abrau/2019/theses/94.pdf, doi:10.20948/abrau-2019-94.
                """;

        String text18 = """
                Батыршина Р.Р. Организация коллекций цифровой математической библиотеки методами семантического анализа / Р.Р. Батыршина, А.М. Елизаров, , Е.К. Липачев // Научный сервис в сети Интернет: труды XXI Всероссийской научной конференции (23-28 сентября 2019 г., г. Новороссийск). — М.: ИПМ им. М.В.Келдыша, 2019. — С. 85-90. — URL: http://keldysh.ru/abrau/2019/theses/97.pdf, doi:10.20948/abrau-2019-97.\s
                """;

        String text19 = """
                Елизаров А.М., Кириллович А.В., Липачев Е.К., Невзорова О.А., Шакирова Л.Р. Семантические технологии в математическом образовании: онтологии и открытые связанные данные // Ученые записки ИСГЗ, 2018. №1. С. 222–227.
                """;

        /*LibraryNode libraryNode = new LibraryNode();
        libraryNode.setTitle("Mathematical knowledge management: ontological models and digital technology");
        System.out.println(scholarParserMetaDataAdditionService.addMetadata(libraryNode));*/

        /*LibraryNode extracted = extracted(text0);
        publisherInformationRepository.saveAll(extracted.getPublisherInformation());
        libraryNodeRepository.save(extracted);
        libraryRepository.save(new Library("test", Collections.singleton(extracted)));*/
        /*LibraryNode extracted = extracted(text5);
        System.out.println(objectMapper.writeValueAsString(extracted));*/

        //extracted(text9);

        /*extracted(text0);
        extracted(text1);
        extracted(text2);
        extracted(text3);
        extracted(text4);
        extracted(text5);
        extracted(text6);
        extracted(text7);
        extracted(text10);
        extracted(text11);
        extracted(text12);
        extracted(text13);
        extracted(text14);
        extracted(text15);
        extracted(text16);
        extracted(text17);
        extracted(text18);
        extracted(text19)*/
        //System.out.println(extracted(text13));
    }

    private static LibraryNode extracted(String libraryRawNode) {
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
        System.out.println(title);

        String textWithoutTitle = textWithoutHeading.substring(title.length()).trim();
        System.out.println(textWithoutTitle);
        List<String> informationRelatedToTheTitle = new ArrayList<>();
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
                System.out.println("mainResourceType " + mainResourceType);
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
            System.out.println("mainResourceTitle: " + mainResourceTitle);
            System.out.println("additionalInformationAboutMainResourceTitle: " + additionalInformationAboutMainResourceTitle);
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
            System.out.println("publicationYear: " + publicationYear);
            System.out.println("publicationNumber: " + publicationNumber);
            System.out.println("volume: " + volume);
        } else {
            String textWithoutInformationAboutResponsibility = textWithoutInformationRelatedToTheTitle.substring(textWithoutInformationRelatedToTheTitle.indexOf("–") + 1).trim();
            if (textWithoutInformationAboutResponsibility.contains("–") && textWithoutInformationAboutResponsibility.indexOf("–") < textWithoutInformationAboutResponsibility.indexOf(":")) {
                informationAboutThePublication = textWithoutInformationAboutResponsibility.split("–")[0].trim();
            }

            String textWithoutInformationAboutThePublication = textWithoutInformationAboutResponsibility;
            if (!informationAboutThePublication.isEmpty()) {
                textWithoutInformationAboutThePublication = textWithoutInformationAboutResponsibility.replaceAll(informationAboutThePublication, "").substring(2).trim();
            }
            System.out.println(textWithoutInformationAboutThePublication);
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
            isUrlActive = checkUrlActive(url);
        }

        LibraryNode libraryNode = new LibraryNode(libraryRawNode, heading.toString(), title, informationRelatedToTheTitle.toString(), informationAboutResponsibility.toString(), additionalInformationAboutResponsibility, mainResourceType, mainResourceTitle, additionalInformationAboutMainResourceTitle, informationAboutThePublication, publisherInformation, publicationYear, publicationNumber, volume, note, isbn, url, isUrlActive, accessModes.toString(), typeOfContent);
        return removeReductions(libraryNode);
    }

    private static Boolean checkUrlActive(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:45.0) Gecko/20100101 Firefox/45.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(URI.create(url.split(" ")[0]), HttpMethod.GET, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    private static LibraryNode removeReductions(LibraryNode libraryNode) {
        for (Map.Entry<String, String> reduction : ReductionUtils.reductions.entrySet()) {
            libraryNode.setTitle(libraryNode.getTitle().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setInformationRelatedToTheTitle(libraryNode.getInformationRelatedToTheTitle().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setInformationAboutResponsibility(libraryNode.getInformationAboutResponsibility().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setAdditionalInformationAboutResponsibility(libraryNode.getAdditionalInformationAboutResponsibility().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setInformationAboutThePublication(libraryNode.getInformationAboutThePublication().replaceAll(reduction.getKey(), reduction.getValue()));
            libraryNode.setPublisherInformation(libraryNode.getPublisherInformation().stream().peek(pi -> {
                pi.setPublisherName(pi.getPublisherName().replaceAll(reduction.getKey(), reduction.getValue()));
                pi.setPublisherName(pi.getPublisherCity().replaceAll(reduction.getKey(), reduction.getValue()));
            }).collect(Collectors.toSet()));
            libraryNode.setVolume(libraryNode.getVolume().replaceAll(reduction.getKey(), reduction.getValue()));
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
