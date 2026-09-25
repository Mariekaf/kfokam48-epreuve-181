package com.kfokam48.backend.exception;

public class SessionInconnueException extends RuntimeException {

    public SessionInconnueException(Long sessionId) {
        super("La session " + sessionId + " est inconnue.");
    }
}
