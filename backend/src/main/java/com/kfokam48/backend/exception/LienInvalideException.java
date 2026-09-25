package com.kfokam48.backend.exception;

public class LienInvalideException extends RuntimeException {

    public LienInvalideException() {
        super("Le lien de l'exercice est invalide.");
    }
}
