package org.zero.aienglish.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DataIncorrectException extends RequestException {
    public DataIncorrectException(String message) {
        super(message);
    }
}
