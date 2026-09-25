package com.kfokam48.backend.exception;

public class PresenceDejaEnregistreeException extends RuntimeException {

    public PresenceDejaEnregistreeException() {
        super("L'etudiant est deja present pour cette session.");
    }
}
