package org.zero.aienglish.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zero.aienglish.model.ExceptionDTO;

@ControllerAdvice
public class RequestExceptionHandler {
    @ExceptionHandler(RequestException.class)
    public ResponseEntity<ExceptionDTO> handleException(RequestException e) {
        return new ResponseEntity<>(new ExceptionDTO(e.getMessage()), HttpStatus.FORBIDDEN);
    }
}
