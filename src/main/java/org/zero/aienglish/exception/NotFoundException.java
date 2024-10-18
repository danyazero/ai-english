package org.zero.aienglish.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RequestException {
    public NotFoundException(String message) {
        super(message);
    }
}
