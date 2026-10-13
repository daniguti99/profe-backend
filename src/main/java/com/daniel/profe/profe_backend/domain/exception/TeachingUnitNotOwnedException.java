package com.daniel.profe.profe_backend.domain.exception;

public class TeachingUnitNotOwnedException extends RuntimeException {
    public TeachingUnitNotOwnedException(String message) {
        super(message);
    }
}
