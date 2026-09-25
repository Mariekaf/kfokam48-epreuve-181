package com.kfokam48.backend.exception;

public class EtudiantHorsPromotionException extends RuntimeException {

    public EtudiantHorsPromotionException(Long etudiantId) {
        super("L'etudiant " + etudiantId + " n'appartient pas a la promotion de la session.");
    }
}
