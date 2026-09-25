package com.kfokam48.backend.exception;

public class RelectureInconnueException extends RuntimeException {

    public RelectureInconnueException(Long relectureId) {
        super("La relecture " + relectureId + " est inconnue.");
    }
}
