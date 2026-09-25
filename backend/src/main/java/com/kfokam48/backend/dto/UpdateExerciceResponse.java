package com.kfokam48.backend.dto;
import com.kfokam48.backend.entity.ExerciceStatus;
public record UpdateExerciceResponse(Long id, String lien, ExerciceStatus statut) {}
