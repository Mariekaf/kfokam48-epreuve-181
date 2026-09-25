package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.CorrectionRelectureRequest;
import com.kfokam48.backend.dto.RelectureResponse;
import com.kfokam48.backend.entity.*;
import com.kfokam48.backend.exception.BusinessException;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.RelectureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class RelectureWorkflowService {
    private final RelectureRepository relectureRepository;
    private final EtudiantRepository etudiantRepository;
    private final Clock clock;

    public RelectureWorkflowService(RelectureRepository relectureRepository, EtudiantRepository etudiantRepository, Clock clock) {
        this.relectureRepository = relectureRepository;
        this.etudiantRepository = etudiantRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<RelectureResponse> lister(Long relecteurId) {
        Etudiant relecteur = etudiantRepository.findById(relecteurId)
                .orElseThrow(() -> new BusinessException("ETUDIANT_INCONNU", "L'étudiant demandé est inconnu.", HttpStatus.NOT_FOUND));
        return relectureRepository.findByRelecteurOrderByAffecteeAtDesc(relecteur).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RelectureResponse detail(Long id, Long relecteurId) {
        Relecture r = get(id);
        verifierRelecteur(r, relecteurId);
        return toResponse(r);
    }

    @Transactional
    public RelectureResponse commencer(Long id, Long relecteurId) {
        Relecture r = get(id);
        verifierRelecteur(r, relecteurId);
        if (r.getStatut() != RelectureStatus.A_FAIRE) {
            throw new BusinessException("RELECTURE_DEJA_COMMENCEE", "Cette relecture est déjà commencée ou rendue.", HttpStatus.CONFLICT);
        }
        r.setStatut(RelectureStatus.EN_COURS);
        r.setCommenceeAt(OffsetDateTime.now(clock));
        r.getExercice().setStatut(ExerciceStatus.EN_COURS);
        return toResponse(relectureRepository.save(r));
    }

    @Transactional
    public void corriger(Long id, CorrectionRelectureRequest request) {
        Relecture r = get(id);
        verifierRelecteur(r, request.relecteurId());
        if (r.getExercice().getSession().getStatut() == SessionStatus.CLOTUREE) {
            throw new BusinessException("SESSION_CLOTUREE", "La session est clôturée : la relecture est définitive.", HttpStatus.CONFLICT);
        }
        if (r.getStatut() != RelectureStatus.RELU) {
            throw new BusinessException("RELECTURE_NON_RENDUE", "La relecture doit d'abord être rendue.", HttpStatus.CONFLICT);
        }
        r.setNote(request.note());
        r.setCommentaire(request.commentaire().trim());
        r.setModifieeAt(OffsetDateTime.now(clock));
        relectureRepository.save(r);
    }

    private Relecture get(Long id) {
        return relectureRepository.findById(id)
                .orElseThrow(() -> new BusinessException("RELECTURE_INCONNUE", "La relecture demandée est inconnue.", HttpStatus.NOT_FOUND));
    }

    private void verifierRelecteur(Relecture r, Long relecteurId) {
        if (!Objects.equals(r.getRelecteur().getId(), relecteurId)) {
            throw new BusinessException("RELECTEUR_NON_AUTORISE", "Vous n'êtes pas le relecteur affecté à cet exercice.", HttpStatus.FORBIDDEN);
        }
    }

    private RelectureResponse toResponse(Relecture r) {
        return new RelectureResponse(r.getId(), r.getExercice().getId(), r.getExercice().getLien(), r.getStatut(), r.getNote(), r.getCommentaire(), r.getCommenceeAt());
    }
}
