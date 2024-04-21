package ru.deevdenis.sessionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DataNotFoundException extends ResponseStatusException {

    public DataNotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }

    public DataNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
