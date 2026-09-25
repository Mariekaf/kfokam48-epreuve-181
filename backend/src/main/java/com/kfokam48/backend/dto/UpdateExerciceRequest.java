package com.kfokam48.backend.dto;
import jakarta.validation.constraints.NotBlank;
public record UpdateExerciceRequest(@NotBlank(message="Le lien est obligatoire.") String lien) {}
