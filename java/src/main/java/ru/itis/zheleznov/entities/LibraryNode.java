package ru.itis.zheleznov.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "library_node")
public class LibraryNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rawValue;

    private String heading;
    private String title;
    private String informationRelatedToTheTitle;
    private String informationAboutResponsibility;
    private String additionalInformationAboutResponsibility;
    private String mainResourceType;
    private String mainResourceTitle;
    private String additionalInformationAboutMainResourceTitle;
    private String informationAboutThePublication;
    @OneToMany
    private Set<PublisherInformation> publisherInformation;
    private String publicationYear;
    private String publicationNumber;
    private String volume;
    private String note;
    private String ISBN;
    private String URL;
    private Boolean isUrlActive;
    private String accessMode;
    private String typeOfContent;
    private Boolean isError;

    public LibraryNode(String rawValue, String heading, String title, String informationRelatedToTheTitle, String informationAboutResponsibility, String additionalInformationAboutResponsibility, String mainResourceType, String mainResourceTitle, String additionalInformationAboutMainResourceTitle, String informationAboutThePublication, Set<PublisherInformation> publisherInformation, String publicationYear, String publicationNumber, String volume, String note, String ISBN, String URL, Boolean isUrlActive, String accessMode, String typeOfContent) {
        this.rawValue = rawValue;
        this.heading = heading;
        this.title = title;
        this.informationRelatedToTheTitle = informationRelatedToTheTitle;
        this.informationAboutResponsibility = informationAboutResponsibility;
        this.additionalInformationAboutResponsibility = additionalInformationAboutResponsibility;
        this.mainResourceType = mainResourceType;
        this.mainResourceTitle = mainResourceTitle;
        this.additionalInformationAboutMainResourceTitle = additionalInformationAboutMainResourceTitle;
        this.informationAboutThePublication = informationAboutThePublication;
        this.publisherInformation = publisherInformation;
        this.publicationYear = publicationYear;
        this.publicationNumber = publicationNumber;
        this.volume = volume;
        this.note = note;
        this.ISBN = ISBN;
        this.URL = URL;
        this.isUrlActive = isUrlActive;
        this.accessMode = accessMode;
        this.typeOfContent = typeOfContent;
        this.isError = false;
    }

    public LibraryNode(String rawValue, String heading, String title, String informationRelatedToTheTitle, String informationAboutResponsibility, String additionalInformationAboutResponsibility, String mainResourceType, String mainResourceTitle, String additionalInformationAboutMainResourceTitle, String informationAboutThePublication, Set<PublisherInformation> publisherInformation, String publicationYear, String publicationNumber, String volume, String note, String ISBN, String URL, Boolean isUrlActive, String accessMode, String typeOfContent, Boolean isError) {
        this.rawValue = rawValue;
        this.heading = heading;
        this.title = title;
        this.informationRelatedToTheTitle = informationRelatedToTheTitle;
        this.informationAboutResponsibility = informationAboutResponsibility;
        this.additionalInformationAboutResponsibility = additionalInformationAboutResponsibility;
        this.mainResourceType = mainResourceType;
        this.mainResourceTitle = mainResourceTitle;
        this.additionalInformationAboutMainResourceTitle = additionalInformationAboutMainResourceTitle;
        this.informationAboutThePublication = informationAboutThePublication;
        this.publisherInformation = publisherInformation;
        this.publicationYear = publicationYear;
        this.publicationNumber = publicationNumber;
        this.volume = volume;
        this.note = note;
        this.ISBN = ISBN;
        this.URL = URL;
        this.isUrlActive = isUrlActive;
        this.accessMode = accessMode;
        this.typeOfContent = typeOfContent;
        this.isError = isError;
    }
}
