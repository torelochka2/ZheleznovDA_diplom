package ru.itis.zheleznov.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "library")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentName;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<LibraryNode> libraryNodes;

    public Library(String documentName, Set<LibraryNode> libraryNodes) {
        this.documentName = documentName;
        this.libraryNodes = libraryNodes;
    }
}
