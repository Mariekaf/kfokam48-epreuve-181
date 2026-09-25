package com.kfokam48.backend.exception;

public class SessionClotureeException extends RuntimeException {

    public SessionClotureeException(Long sessionId) {
        super("La session " + sessionId + " est cloturee.");
    }
}
