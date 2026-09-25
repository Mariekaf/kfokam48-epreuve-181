package com.kfokam48.backend.dto;
import com.kfokam48.backend.entity.SessionStatus;
import java.time.OffsetDateTime;
public record SessionResponse(Long id, String titre, String code, SessionStatus statut, OffsetDateTime ouvertureAt, OffsetDateTime expirationAt, OffsetDateTime finAt, OffsetDateTime clotureAt, Long promotionId) {}
