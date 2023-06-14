package ru.itis.zheleznov.enums;

import ru.itis.zheleznov.exceptions.UnsupportedFileContentTypeException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum FileContentType {
    PDF(Collections.singletonList("application/pdf")),
    DOCS(List.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"));

    private List<String> values;

    private static final Map<FileContentType, List<String>> ENUM_MAP;

    FileContentType(List<String> values) {
        this.values = values;
    }

    static {
        Map<FileContentType, List<String>> map = new ConcurrentHashMap<>();
        for (FileContentType instance : FileContentType.values()) {
            map.put(instance, instance.values);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static FileContentType get(String name) {
        return ENUM_MAP.entrySet().stream()
                .filter(fileContentType -> fileContentType.getValue().contains(name))
                .findFirst().stream()
                .map(Map.Entry::getKey).findFirst()
                .orElseThrow(() -> new UnsupportedFileContentTypeException("Unknown file content type " + name));
    }
}
