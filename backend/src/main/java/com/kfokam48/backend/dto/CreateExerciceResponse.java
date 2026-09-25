package com.kfokam48.backend.dto;

import com.kfokam48.backend.entity.ExerciceStatus;

public record CreateExerciceResponse(

        Long id,
        ExerciceStatus statut

) {
}
