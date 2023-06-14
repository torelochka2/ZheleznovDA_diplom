package ru.itis.zheleznov.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "publisher_information")
public class PublisherInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publisherCity;
    private String publisherName;

    public PublisherInformation(String publisherCity, String publisherName) {
        this.publisherCity = publisherCity;
        this.publisherName = publisherName;
    }
}
