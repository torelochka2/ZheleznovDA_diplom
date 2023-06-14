package ru.itis.zheleznov.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.zheleznov.entities.Library;
import ru.itis.zheleznov.services.FileService;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public Library processFile(@RequestPart MultipartFile file) {
        return fileService.getLibrary(file);
    }
}
