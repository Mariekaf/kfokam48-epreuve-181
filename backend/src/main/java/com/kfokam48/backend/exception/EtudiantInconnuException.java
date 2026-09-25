package com.kfokam48.backend.exception;

public class EtudiantInconnuException extends RuntimeException {

    public EtudiantInconnuException(Long etudiantId) {
        super("L'etudiant " + etudiantId + " est inconnu.");
    }
}
