package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.ExerciceResultatResponse;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Relecture;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.exception.BusinessException;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.ExerciceRepository;
import com.kfokam48.backend.repository.RelectureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentQueryService {
    private final EtudiantRepository etudiantRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;

    public StudentQueryService(EtudiantRepository etudiantRepository, ExerciceRepository exerciceRepository, RelectureRepository relectureRepository) {
        this.etudiantRepository = etudiantRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
    }

    @Transactional(readOnly = true)
    public List<ExerciceResultatResponse> resultats(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new BusinessException("ETUDIANT_INCONNU", "L'étudiant demandé est inconnu.", HttpStatus.NOT_FOUND));
        return exerciceRepository.findByEtudiantOrderByDeposeAtDesc(etudiant).stream().map(exercice -> {
            Relecture relecture = relectureRepository.findByExercice(exercice).orElse(null);
            Integer note = relecture != null && relecture.getStatut() == RelectureStatus.RELU ? relecture.getNote() : null;
            String commentaire = relecture != null && relecture.getStatut() == RelectureStatus.RELU ? relecture.getCommentaire() : null;
            return new ExerciceResultatResponse(exercice.getId(), exercice.getSession().getId(), exercice.getLien(), exercice.getStatut(), note, commentaire);
        }).toList();
    }
}
