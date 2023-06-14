package ru.itis.zheleznov.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Value("${integration.scholar-parser.timeout}")
    private Integer scholarParserReadTimeout;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().setReadTimeout(Duration.ofSeconds(scholarParserReadTimeout)).build();
    }
}
