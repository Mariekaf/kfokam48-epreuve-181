package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.EtudiantResponse;
import com.kfokam48.backend.dto.PromotionResponse;
import com.kfokam48.backend.exception.BusinessException;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReferenceDataService {
    private final PromotionRepository promotionRepository;
    private final EtudiantRepository etudiantRepository;

    public ReferenceDataService(PromotionRepository promotionRepository, EtudiantRepository etudiantRepository) {
        this.promotionRepository = promotionRepository;
        this.etudiantRepository = etudiantRepository;
    }

    public List<PromotionResponse> promotions() {
        return promotionRepository.findAll().stream()
                .map(p -> new PromotionResponse(p.getId(), p.getNom()))
                .toList();
    }

    public List<EtudiantResponse> etudiants(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new BusinessException("PROMOTION_INCONNUE", "La promotion demandée est inconnue.", HttpStatus.NOT_FOUND);
        }
        return etudiantRepository.findByPromotionIdOrderByNomAsc(promotionId).stream()
                .map(e -> new EtudiantResponse(e.getId(), e.getNom()))
                .toList();
    }
}
