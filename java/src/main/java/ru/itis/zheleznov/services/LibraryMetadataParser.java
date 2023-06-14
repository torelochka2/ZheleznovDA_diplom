package ru.itis.zheleznov.services;

import ru.itis.zheleznov.entities.LibraryNode;

public interface LibraryMetadataParser {
    LibraryNode parseMetadata(String libraryRawNode);
}
