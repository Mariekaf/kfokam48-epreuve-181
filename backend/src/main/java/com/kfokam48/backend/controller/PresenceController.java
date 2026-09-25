package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.MarkPresenceRequest;
import com.kfokam48.backend.dto.MarkPresenceResponse;
import com.kfokam48.backend.service.PresenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/presences")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @PostMapping
    public ResponseEntity<MarkPresenceResponse> marquerPresence(
            @Valid @RequestBody MarkPresenceRequest request
    ) {

        MarkPresenceResponse response =
                presenceService.marquerPresence(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
