package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.ExerciceResultatResponse;
import com.kfokam48.backend.service.StudentQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {
    private final StudentQueryService studentQueryService;
    public EtudiantController(StudentQueryService studentQueryService) { this.studentQueryService = studentQueryService; }

    @GetMapping("/{id}/exercices")
    public List<ExerciceResultatResponse> resultats(@PathVariable Long id) {
        return studentQueryService.resultats(id);
    }
}
