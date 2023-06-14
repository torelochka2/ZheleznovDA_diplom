package ru.itis.zheleznov.services.impl;

import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.zheleznov.entities.Library;
import ru.itis.zheleznov.entities.LibraryNode;
import ru.itis.zheleznov.enums.FileContentType;
import ru.itis.zheleznov.services.LibraryMetadataParser;
import ru.itis.zheleznov.services.LibraryParagraphPatternService;
import ru.itis.zheleznov.services.ParsingService;

import java.util.*;

@Service
public class PdfParsingService implements ParsingService {

    private final List<LibraryParagraphPatternService> libraryParagraphPatternServices;
    private final LibraryMetadataParser regexpLibraryMetadataParser;

    public PdfParsingService(List<LibraryParagraphPatternService> libraryParagraphPatternServices, LibraryMetadataParser regexpLibraryMetadataParser) {
        this.libraryParagraphPatternServices = libraryParagraphPatternServices;
        this.regexpLibraryMetadataParser = regexpLibraryMetadataParser;
    }

    @SneakyThrows
    @Override
    public Library parse(MultipartFile file) {
        PDDocument document = PDDocument.load(file.getInputStream());
        Set<LibraryNode> libraryRows = new HashSet<>();

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        boolean isLiterature = false;
        String lineBreaker = "\\r?\\n";
        String literatureBreaker = "\\n[0-9]+[.]\\s";
        List<String> lines = Arrays.stream(text.split(lineBreaker)).toList();
        int i = 0;
        for (String line : lines) {
            i++;
            if (isLibraryParagraph(line.trim())) {
                isLiterature = true;
                break;
            }
        }
        if (isLiterature) {
            String literatureBlock = String.join("\n", lines.subList(i, lines.size()));
            List<String> literatures = Arrays.stream(literatureBlock.split(literatureBreaker)).toList();
            for (String literature : literatures) {
                libraryRows.add(regexpLibraryMetadataParser.parseMetadata(literature.trim()));
            }
        }

        document.close();

        return new Library(file.getName(), libraryRows);
    }

    @Override
    public FileContentType getType() {
        return FileContentType.PDF;
    }

    private boolean isLibraryParagraph(String line) {
        for (LibraryParagraphPatternService libraryParagraphPatternService : libraryParagraphPatternServices) {
            if (libraryParagraphPatternService.isLibraryParagraph(line)) return true;
        }
        return false;
    }
}
