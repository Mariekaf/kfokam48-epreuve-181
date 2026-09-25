package com.kfokam48.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmitRelectureRequest(

        Integer note,

        @NotBlank(message = "Le commentaire est obligatoire.")
        String commentaire

) {
}
