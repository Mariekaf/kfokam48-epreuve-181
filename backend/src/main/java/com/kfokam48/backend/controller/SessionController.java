package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.CreateSessionRequest;
import com.kfokam48.backend.dto.CreateSessionResponse;
import com.kfokam48.backend.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<CreateSessionResponse> ouvrirSession(
            @Valid @RequestBody CreateSessionRequest request
    ) {

        CreateSessionResponse response =
                sessionService.ouvrirSession(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}