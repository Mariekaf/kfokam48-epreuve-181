package com.kfokam48.backend.exception;

public class ExerciceDejaDeposeException extends RuntimeException {

    public ExerciceDejaDeposeException() {
        super("L'etudiant a deja depose un exercice pour cette session.");
    }
}
