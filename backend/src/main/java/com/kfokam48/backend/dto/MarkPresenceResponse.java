package com.kfokam48.backend.dto;

import com.kfokam48.backend.entity.PresenceSource;

public record MarkPresenceResponse(

        Long id,
        Long sessionId,
        Long etudiantId,
        PresenceSource source

) {
}
