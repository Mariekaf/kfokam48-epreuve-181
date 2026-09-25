package com.kfokam48.backend.dto;
import com.kfokam48.backend.entity.ExerciceStatus;
public record ExerciceResultatResponse(Long exerciceId, Long sessionId, String lien, ExerciceStatus statut, Integer note, String commentaire) {}
