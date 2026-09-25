package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.MarkPresenceRequest;
import com.kfokam48.backend.dto.MarkPresenceResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Presence;
import com.kfokam48.backend.entity.PresenceSource;
import com.kfokam48.backend.exception.CodeSessionExpireException;
import com.kfokam48.backend.exception.CodeSessionInconnuException;
import com.kfokam48.backend.exception.EtudiantHorsPromotionException;
import com.kfokam48.backend.exception.EtudiantInconnuException;
import com.kfokam48.backend.exception.PresenceDejaEnregistreeException;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.PresenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class PresenceService {

    private final CourseSessionRepository courseSessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;

    public PresenceService(
            CourseSessionRepository courseSessionRepository,
            EtudiantRepository etudiantRepository,
            PresenceRepository presenceRepository
    ) {
        this.courseSessionRepository = courseSessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
    }

    @Transactional
    public MarkPresenceResponse marquerPresence(MarkPresenceRequest request) {

        CourseSession session = courseSessionRepository
                .findByCode(request.code().trim())
                .orElseThrow(CodeSessionInconnuException::new);

        OffsetDateTime maintenant = OffsetDateTime.now();

        if (!maintenant.isBefore(session.getExpirationAt())) {
            throw new CodeSessionExpireException();
        }

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

        if (presenceRepository.existsBySessionAndEtudiant(session, etudiant)) {
            throw new PresenceDejaEnregistreeException();
        }

        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(PresenceSource.ETUDIANT);
        presence.setEnregistreeAt(maintenant);

        Presence presenceEnregistree = presenceRepository.save(presence);

        return new MarkPresenceResponse(
                presenceEnregistree.getId(),
                session.getId(),
                etudiant.getId(),
                presenceEnregistree.getSource()
        );
    }
}
