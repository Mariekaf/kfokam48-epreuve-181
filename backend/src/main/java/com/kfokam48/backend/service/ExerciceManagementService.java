package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.UpdateExerciceResponse;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.exception.BusinessException;
import com.kfokam48.backend.exception.LienInvalideException;
import com.kfokam48.backend.repository.ExerciceRepository;
import com.kfokam48.backend.repository.RelectureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class ExerciceManagementService {
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;
    private final Clock clock;

    public ExerciceManagementService(ExerciceRepository exerciceRepository, RelectureRepository relectureRepository, Clock clock) {
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
        this.clock = clock;
    }

    @Transactional
    public UpdateExerciceResponse remplacerLien(Long id, String lienBrut) {
        String lien = validerLien(lienBrut);
        Exercice exercice = exerciceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EXERCICE_INCONNU", "L'exercice demandé est inconnu.", HttpStatus.NOT_FOUND));
        if (exercice.getSession().getStatut() == SessionStatus.CLOTUREE) {
            throw new BusinessException("SESSION_CLOTUREE", "La session est clôturée.", HttpStatus.CONFLICT);
        }
        relectureRepository.findByExercice(exercice).ifPresent(r -> {
            if (r.getStatut() == RelectureStatus.EN_COURS || r.getStatut() == RelectureStatus.RELU) {
                throw new BusinessException("RELECTURE_DEJA_COMMENCEE", "Le lien ne peut plus être remplacé car la relecture a commencé.", HttpStatus.CONFLICT);
            }
        });
        exercice.setLien(lien);
        exercice.setModifieAt(OffsetDateTime.now(clock));
        Exercice saved = exerciceRepository.save(exercice);
        return new UpdateExerciceResponse(saved.getId(), saved.getLien(), saved.getStatut());
    }

    private String validerLien(String value) {
        try {
            String lien = value == null ? "" : value.trim();
            URI uri = URI.create(lien);
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) || uri.getHost() == null) {
                throw new LienInvalideException();
            }
            return lien;
        } catch (IllegalArgumentException e) {
            throw new LienInvalideException();
        }
    }
}
