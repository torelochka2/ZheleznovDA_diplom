package ru.itis.zheleznov.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UnsupportedFileContentTypeException extends RuntimeException {

    public UnsupportedFileContentTypeException(String message) {
        super(message);
    }
}
