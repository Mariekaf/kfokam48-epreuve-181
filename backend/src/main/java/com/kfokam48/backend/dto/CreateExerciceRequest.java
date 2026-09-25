package com.kfokam48.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateExerciceRequest(

        @NotNull(message = "La session est obligatoire.")
        @Positive(message = "L'identifiant de la session doit etre positif.")
        Long sessionId,

        @NotNull(message = "L'etudiant est obligatoire.")
        @Positive(message = "L'identifiant de l'etudiant doit etre positif.")
        Long etudiantId,

        @Size(max = 2048, message = "Le lien de l'exercice ne doit pas depasser 2048 caracteres.")
        String lien

) {
}
