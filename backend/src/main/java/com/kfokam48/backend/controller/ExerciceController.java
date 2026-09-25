package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.CreateExerciceRequest;
import com.kfokam48.backend.dto.CreateExerciceResponse;
import com.kfokam48.backend.dto.UpdateExerciceRequest;
import com.kfokam48.backend.dto.UpdateExerciceResponse;
import com.kfokam48.backend.service.ExerciceManagementService;
import com.kfokam48.backend.service.ExerciceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {
    private final ExerciceService exerciceService;
    private final ExerciceManagementService managementService;

    public ExerciceController(ExerciceService exerciceService, ExerciceManagementService managementService) {
        this.exerciceService = exerciceService;
        this.managementService = managementService;
    }

    @PostMapping
    public ResponseEntity<CreateExerciceResponse> deposerExercice(@Valid @RequestBody CreateExerciceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciceService.deposerExercice(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateExerciceResponse> remplacerLien(@PathVariable Long id, @Valid @RequestBody UpdateExerciceRequest request) {
        return ResponseEntity.ok(managementService.remplacerLien(id, request.lien()));
    }
}
