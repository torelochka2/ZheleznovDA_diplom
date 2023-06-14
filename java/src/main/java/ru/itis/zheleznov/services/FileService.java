package ru.itis.zheleznov.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.zheleznov.entities.Library;
import ru.itis.zheleznov.entities.LibraryNode;
import ru.itis.zheleznov.enums.FileContentType;
import ru.itis.zheleznov.exceptions.UnsupportedParsingException;
import ru.itis.zheleznov.repositories.LibraryNodeRepository;
import ru.itis.zheleznov.repositories.LibraryRepository;
import ru.itis.zheleznov.repositories.PublisherInformationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final List<ParsingService> parsingServices;
    private final LibraryRepository libraryRepository;
    private final LibraryNodeRepository libraryNodeRepository;
    private final PublisherInformationRepository publisherInformationRepository;

    public FileService(List<ParsingService> parsingServices, LibraryRepository libraryRepository, LibraryNodeRepository libraryNodeRepository, PublisherInformationRepository publisherInformationRepository) {
        this.parsingServices = parsingServices;
        this.libraryRepository = libraryRepository;
        this.libraryNodeRepository = libraryNodeRepository;
        this.publisherInformationRepository = publisherInformationRepository;
    }


    public Library getLibrary(MultipartFile file) {
        String contentType = file.getContentType();
        FileContentType fileContentType = FileContentType.get(contentType);
        Library library = getParsingService(contentType, fileContentType).parse(file);
        for (LibraryNode node : library.getLibraryNodes()) {
            publisherInformationRepository.saveAll(node.getPublisherInformation());
            libraryNodeRepository.save(node);
        }
        return libraryRepository.save(library);
    }

    private ParsingService getParsingService(String contentType, FileContentType fileContentType) {
        return parsingServices.stream()
                .filter(parsingService -> parsingService.getType() == fileContentType).findFirst()
                .orElseThrow(() -> new UnsupportedParsingException("Can't parsed file with content type " + contentType));
    }
}
