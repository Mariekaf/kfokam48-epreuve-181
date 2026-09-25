package com.kfokam48.backend.exception;

public class NoteInvalideException extends RuntimeException {

    public NoteInvalideException() {
        super("La note doit etre un entier compris entre 0 et 20.");
    }
}
