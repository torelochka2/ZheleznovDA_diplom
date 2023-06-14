package ru.itis.zheleznov.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.itis.zheleznov.dto.ExceptionDto;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            IllegalArgumentException.class, IllegalStateException.class,
            UnsupportedParsingException.class, UnsupportedFileContentTypeException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage(), LocalDateTime.now());
        return handleExceptionInternal(ex, exceptionDto, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}