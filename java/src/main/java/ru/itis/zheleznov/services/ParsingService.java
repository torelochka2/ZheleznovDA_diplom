package ru.itis.zheleznov.services;

import org.springframework.web.multipart.MultipartFile;
import ru.itis.zheleznov.entities.Library;
import ru.itis.zheleznov.enums.FileContentType;

public interface ParsingService {
    Library parse(MultipartFile file);
    FileContentType getType();
}
