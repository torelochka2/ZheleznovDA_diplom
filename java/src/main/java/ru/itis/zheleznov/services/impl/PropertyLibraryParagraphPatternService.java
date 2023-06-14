package ru.itis.zheleznov.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.zheleznov.services.LibraryParagraphPatternService;

import java.util.List;

@Service
public class PropertyLibraryParagraphPatternService implements LibraryParagraphPatternService {

    @Value("${library.patterns.paragraphs}")
    private List<String> paragraphPatterns;

    @Override
    public Boolean isLibraryParagraph(String paragraph) {
        return paragraphPatterns.stream().map(pattern -> pattern.toLowerCase().trim())
                .toList().contains(paragraph.toLowerCase().trim());
    }
}
