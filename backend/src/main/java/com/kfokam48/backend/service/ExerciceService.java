package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.CreateExerciceRequest;
import com.kfokam48.backend.dto.CreateExerciceResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.ExerciceStatus;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.exception.EtudiantHorsPromotionException;
import com.kfokam48.backend.exception.EtudiantInconnuException;
import com.kfokam48.backend.exception.ExerciceDejaDeposeException;
import com.kfokam48.backend.exception.LienInvalideException;
import com.kfokam48.backend.exception.SessionClotureeException;
import com.kfokam48.backend.exception.SessionInconnueException;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.ExerciceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class ExerciceService {

    private final CourseSessionRepository courseSessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureAffectationService relectureAffectationService;
    private final Clock clock;

    public ExerciceService(
            CourseSessionRepository courseSessionRepository,
            EtudiantRepository etudiantRepository,
            ExerciceRepository exerciceRepository,
            RelectureAffectationService relectureAffectationService,
            Clock clock
    ) {
        this.courseSessionRepository = courseSessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureAffectationService = relectureAffectationService;
        this.clock = clock;
    }

    @Transactional
    public CreateExerciceResponse deposerExercice(
            CreateExerciceRequest request
    ) {

        String lien = validerEtNormaliserLien(request.lien());

        CourseSession session = courseSessionRepository
                .findById(request.sessionId())
                .orElseThrow(() ->
                        new SessionInconnueException(request.sessionId())
                );

        Etudiant etudiant = etudiantRepository.findById(request.etudiantId())
                .orElseThrow(() ->
                        new EtudiantInconnuException(request.etudiantId())
                );

        if (!Objects.equals(
                session.getPromotion().getId(),
                etudiant.getPromotion().getId()
        )) {
            throw new EtudiantHorsPromotionException(etudiant.getId());
        }

        if (SessionStatus.CLOTUREE.equals(session.getStatut())) {
            throw new SessionClotureeException(session.getId());
        }

        if (exerciceRepository.existsBySessionAndEtudiant(session, etudiant)) {
            throw new ExerciceDejaDeposeException();
        }

        Exercice exercice = new Exercice();
        exercice.setLien(lien);
        exercice.setStatut(ExerciceStatus.DEPOSE);
        exercice.setDeposeAt(OffsetDateTime.now(clock));
        exercice.setSession(session);
        exercice.setEtudiant(etudiant);

        Exercice exerciceEnregistre = exerciceRepository.save(exercice);
        relectureAffectationService.affecterRelecteurSiPossible(
                exerciceEnregistre
        );

        return new CreateExerciceResponse(
                exerciceEnregistre.getId(),
                exerciceEnregistre.getStatut()
        );
    }

    private String validerEtNormaliserLien(String lien) {

        if (lien == null || lien.trim().isEmpty()) {
            throw new LienInvalideException();
        }

        String lienNormalise = lien.trim();

        try {
            URI uri = new URI(lienNormalise);
            String scheme = uri.getScheme();

            if (!("http".equalsIgnoreCase(scheme)
                    || "https".equalsIgnoreCase(scheme))
                    || uri.getHost() == null) {
                throw new LienInvalideException();
            }
        } catch (URISyntaxException exception) {
            throw new LienInvalideException();
        }

        return lienNormalise;
    }
}
