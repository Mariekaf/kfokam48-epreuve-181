package com.kfokam48.backend.dto;

import java.time.OffsetDateTime;

public record CreateSessionResponse(

        Long id,
        String code,
        OffsetDateTime ouvertureAt,
        OffsetDateTime expirationAt

) {
}