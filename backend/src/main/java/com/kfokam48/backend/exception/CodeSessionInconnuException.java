package com.kfokam48.backend.exception;

public class CodeSessionInconnuException extends RuntimeException {

    public CodeSessionInconnuException() {
        super("Le code de session est inconnu.");
    }
}
