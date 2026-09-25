package com.daniel.profe.profe_backend.domain.exception;

public class PasswordMismatchException
        extends RuntimeException {

    public PasswordMismatchException(String message) {
        super(message);
    }
}
