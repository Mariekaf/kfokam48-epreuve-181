package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.CreateSessionRequest;
import com.kfokam48.backend.dto.CreateSessionResponse;
import com.kfokam48.backend.dto.ManualPresenceRequest;
import com.kfokam48.backend.dto.MarkPresenceResponse;
import com.kfokam48.backend.dto.SessionResponse;
import com.kfokam48.backend.service.PresenceService;
import com.kfokam48.backend.service.SessionService;
import com.kfokam48.backend.service.SessionWorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final SessionWorkflowService workflowService;
    private final PresenceService presenceService;

    public SessionController(SessionService sessionService, SessionWorkflowService workflowService, PresenceService presenceService) {
        this.sessionService = sessionService;
        this.workflowService = workflowService;
        this.presenceService = presenceService;
    }

    @PostMapping
    public ResponseEntity<CreateSessionResponse> ouvrirSession(@Valid @RequestBody CreateSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.ouvrirSession(request));
    }

    @GetMapping
    public List<SessionResponse> lister(@RequestParam Long promotionId) {
        return workflowService.lister(promotionId);
    }

    @PostMapping("/{id}/fin")
    public ResponseEntity<SessionResponse> terminer(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.terminer(id));
    }

    @PostMapping("/{id}/cloture")
    public ResponseEntity<SessionResponse> cloturer(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.cloturer(id));
    }

    @PostMapping("/{id}/presences")
    public ResponseEntity<MarkPresenceResponse> ajouterPresence(
            @PathVariable Long id,
            @Valid @RequestBody ManualPresenceRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(presenceService.ajouterManuellement(id, request.etudiantId()));
    }
}
