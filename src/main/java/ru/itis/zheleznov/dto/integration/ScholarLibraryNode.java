package ru.itis.zheleznov.dto.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScholarLibraryNode {
    private String title;
    private String pages;
    private String pub_year;
    private String author;
}
