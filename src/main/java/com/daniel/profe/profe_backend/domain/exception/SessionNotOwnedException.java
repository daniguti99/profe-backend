package com.daniel.profe.profe_backend.domain.exception;

public class SessionNotOwnedException extends RuntimeException {
    public SessionNotOwnedException(String message) {
        super(message);
    }
}
