package com.kfokam48.backend.exception;

public class RelectureDejaRendueException extends RuntimeException {

    public RelectureDejaRendueException() {
        super("La relecture a deja ete rendue.");
    }
}
