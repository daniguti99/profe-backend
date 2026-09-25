package com.daniel.profe.profe_backend.domain.exception;

public class EmailMismatchException
        extends RuntimeException {

    public EmailMismatchException(String message) {
        super(message);
    }
}
