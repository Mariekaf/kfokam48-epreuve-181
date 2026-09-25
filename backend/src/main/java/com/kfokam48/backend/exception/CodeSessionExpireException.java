package com.kfokam48.backend.exception;

public class CodeSessionExpireException extends RuntimeException {

    public CodeSessionExpireException() {
        super("Le code de presence a expire.");
    }
}
