package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.EtudiantResponse;
import com.kfokam48.backend.dto.PromotionResponse;
import com.kfokam48.backend.service.ReferenceDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReferenceDataController {
    private final ReferenceDataService referenceDataService;

    public ReferenceDataController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/promotions")
    public List<PromotionResponse> promotions() {
        return referenceDataService.promotions();
    }

    @GetMapping("/etudiants")
    public List<EtudiantResponse> etudiants(@RequestParam Long promotionId) {
        return referenceDataService.etudiants(promotionId);
    }
}
