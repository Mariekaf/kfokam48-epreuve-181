package com.kfokam48.backend.dto;
import jakarta.validation.constraints.NotNull;
public record ManualPresenceRequest(@NotNull(message="L'étudiant est obligatoire.") Long etudiantId) {}
