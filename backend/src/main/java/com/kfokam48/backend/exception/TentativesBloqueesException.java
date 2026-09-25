package com.kfokam48.backend.exception;

public class TentativesBloqueesException extends RuntimeException {

    public TentativesBloqueesException() {
        super("Trop de tentatives incorrectes. R\u00e9essayez dans 2 minutes.");
    }
}
