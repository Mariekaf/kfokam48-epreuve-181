package com.kfokam48.backend.dto;

public record TableauEtudiantResponse(
        Long etudiantId,
        String nom,
        long presences,
        long exercicesDeposes,
        Double moyenne,
        long relecturesEnAttente
) {
}