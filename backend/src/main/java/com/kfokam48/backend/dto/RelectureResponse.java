package com.kfokam48.backend.dto;
import com.kfokam48.backend.entity.RelectureStatus;
import java.time.OffsetDateTime;
public record RelectureResponse(Long id, Long exerciceId, String lien, RelectureStatus statut, Integer note, String commentaire, OffsetDateTime commenceeAt) {}
