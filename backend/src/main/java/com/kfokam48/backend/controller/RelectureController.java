package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.SubmitRelectureRequest;
import com.kfokam48.backend.service.RelectureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relectures")
public class RelectureController {

    private final RelectureService relectureService;

    public RelectureController(RelectureService relectureService) {
        this.relectureService = relectureService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> rendreRelecture(
            @PathVariable Long id,
            @Valid @RequestBody SubmitRelectureRequest request
    ) {

        relectureService.rendreRelecture(id, request);

        return ResponseEntity.ok().build();
    }
}
