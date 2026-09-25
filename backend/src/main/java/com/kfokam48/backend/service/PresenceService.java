package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.MarkPresenceRequest;
import com.kfokam48.backend.dto.MarkPresenceResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Presence;
import com.kfokam48.backend.entity.PresenceSource;
import com.kfokam48.backend.entity.TentativePresence;
import com.kfokam48.backend.exception.CodeSessionExpireException;
import com.kfokam48.backend.exception.CodeSessionInconnuException;
import com.kfokam48.backend.exception.EtudiantHorsPromotionException;
import com.kfokam48.backend.exception.EtudiantInconnuException;
import com.kfokam48.backend.exception.PresenceDejaEnregistreeException;
import com.kfokam48.backend.exception.TentativesBloqueesException;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.ExerciceRepository;
import com.kfokam48.backend.repository.PresenceRepository;
import com.kfokam48.backend.repository.TentativePresenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class PresenceService {

    private static final int NOMBRE_MAX_TENTATIVES_INCORRECTES = 5;
    private static final int DUREE_BLOCAGE_MINUTES = 2;

    private final CourseSessionRepository courseSessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;
    private final TentativePresenceRepository tentativePresenceRepository;
    private final Clock clock;
    private ExerciceRepository exerciceRepository;
    private RelectureAffectationService relectureAffectationService;

    public PresenceService(
            CourseSessionRepository courseSessionRepository,
            EtudiantRepository etudiantRepository,
            PresenceRepository presenceRepository,
            TentativePresenceRepository tentativePresenceRepository,
            Clock clock
    ) {
        this.courseSessionRepository = courseSessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
        this.tentativePresenceRepository = tentativePresenceRepository;
        this.clock = clock;
    }

    @Autowired
    public void setAffectationSupport(ExerciceRepository exerciceRepository, RelectureAffectationService relectureAffectationService) {
        this.exerciceRepository = exerciceRepository;
        this.relectureAffectationService = relectureAffectationService;
    }

    @Transactional(noRollbackFor = {
            CodeSessionExpireException.class,
            CodeSessionInconnuException.class,
            EtudiantHorsPromotionException.class,
            PresenceDejaEnregistreeException.class,
            TentativesBloqueesException.class
    })
    public MarkPresenceResponse marquerPresence(MarkPresenceRequest request) {

        OffsetDateTime maintenant = OffsetDateTime.now(clock);

        Etudiant etudiant = etudiantRepository.findById(request.etudiantId())
                .orElseThrow(() ->
                        new EtudiantInconnuException(request.etudiantId())
                );

        TentativePresence tentativePresence =
                recupererOuCreerTentativePresence(etudiant, maintenant);

        verifierBlocage(tentativePresence, maintenant);

        CourseSession session = courseSessionRepository
                .findByCode(request.code().trim())
                .orElseThrow(() -> {
                    enregistrerCodeIncorrect(tentativePresence, maintenant);
                    return new CodeSessionInconnuException();
                });

        if (!com.kfokam48.backend.entity.SessionStatus.OUVERTE.equals(session.getStatut())) {
            throw new com.kfokam48.backend.exception.BusinessException(
                    "SESSION_TERMINEE", "La période de présence de cette session est terminée.", org.springframework.http.HttpStatus.CONFLICT);
        }

        if (!maintenant.isBefore(session.getExpirationAt())) {
            throw new CodeSessionExpireException();
        }

        if (!Objects.equals(
                session.getPromotion().getId(),
                etudiant.getPromotion().getId()
        )) {
            throw new EtudiantHorsPromotionException(etudiant.getId());
        }

        if (presenceRepository.existsBySessionAndEtudiant(session, etudiant)) {
            throw new PresenceDejaEnregistreeException();
        }

        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(PresenceSource.ETUDIANT);
        presence.setEnregistreeAt(maintenant);

        Presence presenceEnregistree = presenceRepository.save(presence);
        reinitialiserTentatives(tentativePresence, maintenant);
        retenterAffectations(session);

        return new MarkPresenceResponse(
                presenceEnregistree.getId(),
                session.getId(),
                etudiant.getId(),
                presenceEnregistree.getSource()
        );
    }

    @Transactional
    public MarkPresenceResponse ajouterManuellement(Long sessionId, Long etudiantId) {
        OffsetDateTime maintenant = OffsetDateTime.now(clock);
        CourseSession session = courseSessionRepository.findById(sessionId)
                .orElseThrow(() -> new com.kfokam48.backend.exception.BusinessException(
                        "SESSION_INCONNUE", "La session demandée est inconnue.", org.springframework.http.HttpStatus.NOT_FOUND));
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new com.kfokam48.backend.exception.BusinessException(
                        "ETUDIANT_INCONNU", "L'étudiant demandé est inconnu.", org.springframework.http.HttpStatus.NOT_FOUND));
        if (!Objects.equals(session.getPromotion().getId(), etudiant.getPromotion().getId())) {
            throw new EtudiantHorsPromotionException(etudiant.getId());
        }
        if (!com.kfokam48.backend.entity.SessionStatus.OUVERTE.equals(session.getStatut())) {
            throw new com.kfokam48.backend.exception.BusinessException(
                    "SESSION_TERMINEE", "Une présence ne peut plus être ajoutée après la fin de la session.", org.springframework.http.HttpStatus.CONFLICT);
        }
        if (presenceRepository.existsBySessionAndEtudiant(session, etudiant)) {
            throw new PresenceDejaEnregistreeException();
        }
        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(PresenceSource.FORMATEUR);
        presence.setEnregistreeAt(maintenant);
        Presence saved = presenceRepository.save(presence);
        retenterAffectations(session);
        return new MarkPresenceResponse(saved.getId(), session.getId(), etudiant.getId(), PresenceSource.FORMATEUR);
    }

    private void retenterAffectations(CourseSession session) {
        if (exerciceRepository == null || relectureAffectationService == null) return;
        exerciceRepository.findBySession(session).forEach(relectureAffectationService::affecterRelecteurSiPossible);
    }

    private TentativePresence recupererOuCreerTentativePresence(
            Etudiant etudiant,
            OffsetDateTime maintenant
    ) {

        return tentativePresenceRepository.findByEtudiant(etudiant)
                .orElseGet(() -> new TentativePresence(etudiant, maintenant));
    }

    private void verifierBlocage(
            TentativePresence tentativePresence,
            OffsetDateTime maintenant
    ) {

        OffsetDateTime bloqueJusqua = tentativePresence.getBloqueJusqua();

        if (bloqueJusqua == null) {
            return;
        }

        if (maintenant.isBefore(bloqueJusqua)) {
            throw new TentativesBloqueesException();
        }

        reinitialiserTentatives(tentativePresence, maintenant);
    }

    private void enregistrerCodeIncorrect(
            TentativePresence tentativePresence,
            OffsetDateTime maintenant
    ) {

        int nombreTentatives =
                tentativePresence.getNombreTentativesIncorrectes() + 1;

        tentativePresence.setNombreTentativesIncorrectes(nombreTentatives);
        tentativePresence.setUpdatedAt(maintenant);

        if (nombreTentatives >= NOMBRE_MAX_TENTATIVES_INCORRECTES) {
            tentativePresence.setBloqueJusqua(
                    maintenant.plusMinutes(DUREE_BLOCAGE_MINUTES)
            );
            tentativePresenceRepository.save(tentativePresence);
            throw new TentativesBloqueesException();
        }

        tentativePresenceRepository.save(tentativePresence);
    }

    private void reinitialiserTentatives(
            TentativePresence tentativePresence,
            OffsetDateTime maintenant
    ) {

        tentativePresence.setNombreTentativesIncorrectes(0);
        tentativePresence.setBloqueJusqua(null);
        tentativePresence.setUpdatedAt(maintenant);
        tentativePresenceRepository.save(tentativePresence);
    }
}
