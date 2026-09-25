package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.TableauEtudiantResponse;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.exception.PromotionTableauInconnueException;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.ExerciceRepository;
import com.kfokam48.backend.repository.PresenceRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import com.kfokam48.backend.repository.RelectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TableauService {

    private final PromotionRepository promotionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;

    public TableauService(
            PromotionRepository promotionRepository,
            EtudiantRepository etudiantRepository,
            PresenceRepository presenceRepository,
            ExerciceRepository exerciceRepository,
            RelectureRepository relectureRepository
    ) {
        this.promotionRepository = promotionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
    }

    @Transactional(readOnly = true)
    public List<TableauEtudiantResponse> consulterTableau(Long promotionId) {

        if (!promotionRepository.existsById(promotionId)) {
            throw new PromotionTableauInconnueException(promotionId);
        }

        List<Etudiant> etudiants =
                etudiantRepository.findByPromotionIdOrderByNomAsc(promotionId);

        return etudiants.stream()
                .map(this::construireLigne)
                .toList();
    }

    private TableauEtudiantResponse construireLigne(Etudiant etudiant) {

        long presences =
                presenceRepository.countByEtudiant(etudiant);

        long exercicesDeposes =
                exerciceRepository.countByEtudiant(etudiant);

        Double moyenne =
                relectureRepository.calculerMoyenneNotesRecues(
                        etudiant,
                        RelectureStatus.RELU
                );

        long relecturesEnAttente =
                relectureRepository.countByRelecteurAndStatutIn(
                        etudiant,
                        List.of(
                                RelectureStatus.A_FAIRE,
                                RelectureStatus.EN_COURS
                        )
                );

        return new TableauEtudiantResponse(
                etudiant.getId(),
                etudiant.getNom(),
                presences,
                exercicesDeposes,
                moyenne,
                relecturesEnAttente
        );
    }
}