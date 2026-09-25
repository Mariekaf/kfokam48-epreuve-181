package com.kfokam48.backend.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record CorrectionRelectureRequest(@NotNull Long relecteurId, @NotNull @Min(0) @Max(20) Integer note, @NotBlank String commentaire) {}
