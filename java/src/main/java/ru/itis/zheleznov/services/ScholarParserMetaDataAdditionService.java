package ru.itis.zheleznov.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.zheleznov.dto.integration.ScholarLibraryNode;
import ru.itis.zheleznov.entities.LibraryNode;

import java.net.URI;
import java.util.Objects;

@Service
@Slf4j
public class ScholarParserMetaDataAdditionService implements MetaDataAdditionService {

    @Value("${integration.scholar-parser.uri}")
    private String scholarParserUri;

    private final RestTemplate restTemplate;

    public ScholarParserMetaDataAdditionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public LibraryNode addMetadata(LibraryNode node) {
        if (Objects.nonNull(node.getTitle()) && !node.getTitle().isBlank()) {
            URI uri = UriComponentsBuilder.fromUriString(scholarParserUri)
                    .queryParam("title", node.getTitle())
                    .queryParam("use_proxy", false)
                    .build().toUri();
            ResponseEntity<ScholarLibraryNode> scholarLibraryNodeResponseEntity = restTemplate.postForEntity(uri, null, ScholarLibraryNode.class);

            if (scholarLibraryNodeResponseEntity.getStatusCode().is2xxSuccessful()) {
                return setFields(node, scholarLibraryNodeResponseEntity.getBody());
            } else {
                log.info("Error on scholarParser api call, statusCode: " + scholarLibraryNodeResponseEntity.getStatusCode().value());
                return node;
            }
        }
        log.error("Node title is null or blank");
        return node;
    }

    private LibraryNode setFields(LibraryNode node, ScholarLibraryNode body) {
        if (Objects.isNull(node.getVolume()) && !Objects.isNull(body.getPages())) {
            node.setVolume(body.getPages());
            log.info("Set pages from scholar");
        }
        if (Objects.isNull(node.getPublicationYear()) && !Objects.isNull(body.getPub_year())) {
            node.setPublicationYear(body.getPub_year());
            log.info("Set publication year from scholar");
        }
        if (Objects.isNull(node.getInformationAboutResponsibility()) && !Objects.isNull(body.getAuthor())) {
            node.setInformationAboutResponsibility(body.getAuthor());
            log.info("Set authors from scholar");
        }
        return node;
    }
}
