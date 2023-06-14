package ru.itis.zheleznov.services.impl;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.zheleznov.entities.Library;
import ru.itis.zheleznov.entities.LibraryNode;
import ru.itis.zheleznov.enums.FileContentType;
import ru.itis.zheleznov.services.LibraryMetadataParser;
import ru.itis.zheleznov.services.LibraryParagraphPatternService;
import ru.itis.zheleznov.services.ParsingService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DocsParsingService implements ParsingService {

    private final List<LibraryParagraphPatternService> libraryParagraphPatternServices;
    private final LibraryMetadataParser regexpLibraryMetadataParser;

    public DocsParsingService(List<LibraryParagraphPatternService> libraryParagraphPatternServices, LibraryMetadataParser regexpLibraryMetadataParser) {
        this.libraryParagraphPatternServices = libraryParagraphPatternServices;
        this.regexpLibraryMetadataParser = regexpLibraryMetadataParser;
    }

    @Override
    public Library parse(MultipartFile file) {
        Set<LibraryNode> libraryRows = new HashSet<>();
        try {
            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(file.getInputStream()));
            List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
            boolean isLiterature = false;

            for (XWPFParagraph paragraph : paragraphList) {
                if (paragraph.isEmpty()) continue;
                if (!isLiterature) {
                    isLiterature = isLibraryParagraph(paragraph);
                } else {
                    libraryRows.add(regexpLibraryMetadataParser.parseMetadata(paragraph.getText().trim()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Library(file.getName(), libraryRows);
    }

    private boolean isLibraryParagraph(XWPFParagraph paragraph) {
        for (LibraryParagraphPatternService libraryParagraphPatternService : libraryParagraphPatternServices) {
            if (libraryParagraphPatternService.isLibraryParagraph(paragraph.getText())) return true;
        }
        return false;
    }

    @Override
    public FileContentType getType() {
        return FileContentType.DOCS;
    }
}
