package com.kfokam48.backend.dto;
import jakarta.validation.constraints.NotNull;
public record StartRelectureRequest(@NotNull(message="Le relecteur est obligatoire.") Long relecteurId) {}
