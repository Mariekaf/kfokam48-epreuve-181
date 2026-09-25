package com.kfokam48.backend.exception;

public class AutoRelectureException extends RuntimeException {

    public AutoRelectureException() {
        super("Un etudiant ne peut pas relire son propre exercice.");
    }
}
