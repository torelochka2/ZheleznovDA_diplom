package ru.itis.zheleznov.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UnsupportedParsingException extends RuntimeException {

    public UnsupportedParsingException(String message) {
        super(message);
    }
}
