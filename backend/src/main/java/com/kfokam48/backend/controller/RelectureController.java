package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.*;
import com.kfokam48.backend.service.RelectureService;
import com.kfokam48.backend.service.RelectureWorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relectures")
public class RelectureController {
    private final RelectureService relectureService;
    private final RelectureWorkflowService workflowService;

    public RelectureController(RelectureService relectureService, RelectureWorkflowService workflowService) {
        this.relectureService = relectureService;
        this.workflowService = workflowService;
    }

    @GetMapping
    public List<RelectureResponse> lister(@RequestParam Long relecteurId) {
        return workflowService.lister(relecteurId);
    }

    @GetMapping("/{id}/detail")
    public RelectureResponse detail(@PathVariable Long id, @RequestParam Long relecteurId) {
        return workflowService.detail(id, relecteurId);
    }

    @PostMapping("/{id}/commencer")
    public RelectureResponse commencer(@PathVariable Long id, @Valid @RequestBody StartRelectureRequest request) {
        return workflowService.commencer(id, request.relecteurId());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> rendreRelecture(@PathVariable Long id, @Valid @RequestBody SubmitRelectureRequest request) {
        relectureService.rendreRelecture(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/correction")
    public ResponseEntity<Void> corriger(@PathVariable Long id, @Valid @RequestBody CorrectionRelectureRequest request) {
        workflowService.corriger(id, request);
        return ResponseEntity.ok().build();
    }
}
