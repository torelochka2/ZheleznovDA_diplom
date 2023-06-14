package ru.itis.zheleznov.services.impl;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itis.zheleznov.services.UrlActivityService;

import java.net.URI;

@Service
public class SimpleUrlActivityService implements UrlActivityService {

    @Override
    public Boolean checkUrlActive(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(URI.create(url.split(" ")[0]), HttpMethod.GET, HttpEntity.EMPTY, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
