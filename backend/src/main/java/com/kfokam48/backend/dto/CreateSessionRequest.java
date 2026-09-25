package com.kfokam48.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateSessionRequest(

        @NotBlank(message = "Le titre est obligatoire.")
        @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères.")
        String titre,

        @NotNull(message = "La promotion est obligatoire.")
        @Positive(message = "L'identifiant de la promotion doit être positif.")
        Long promotionId

) {
}