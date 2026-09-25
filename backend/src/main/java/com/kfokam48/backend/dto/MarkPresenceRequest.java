package com.kfokam48.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MarkPresenceRequest(

        @NotBlank(message = "Le code de session est obligatoire.")
        String code,

        @NotNull(message = "L'etudiant est obligatoire.")
        @Positive(message = "L'identifiant de l'etudiant doit etre positif.")
        Long etudiantId

) {
}
