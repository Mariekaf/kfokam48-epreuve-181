package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.CreateSessionRequest;
import com.kfokam48.backend.dto.CreateSessionResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.exception.PromotionInconnueException;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
public class SessionService {

    private static final int DUREE_VALIDITE_CODE_MINUTES = 15;
    private static final int LONGUEUR_CODE = 6;

    private static final String CARACTERES =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final PromotionRepository promotionRepository;
    private final CourseSessionRepository courseSessionRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public SessionService(
            PromotionRepository promotionRepository,
            CourseSessionRepository courseSessionRepository
    ) {
        this.promotionRepository = promotionRepository;
        this.courseSessionRepository = courseSessionRepository;
    }

    @Transactional
    public CreateSessionResponse ouvrirSession(CreateSessionRequest request) {

        Promotion promotion = promotionRepository.findById(request.promotionId())
                .orElseThrow(() ->
                        new PromotionInconnueException(request.promotionId())
                );

        OffsetDateTime ouvertureAt = OffsetDateTime.now();
        OffsetDateTime expirationAt =
                ouvertureAt.plusMinutes(DUREE_VALIDITE_CODE_MINUTES);

        CourseSession session = new CourseSession();

        session.setTitre(request.titre().trim());
        session.setCode(genererCodeUnique());
        session.setOuvertureAt(ouvertureAt);
        session.setExpirationAt(expirationAt);
        session.setStatut(SessionStatus.OUVERTE);
        session.setPromotion(promotion);

        CourseSession sessionEnregistree =
                courseSessionRepository.save(session);

        return new CreateSessionResponse(
                sessionEnregistree.getId(),
                sessionEnregistree.getCode(),
                sessionEnregistree.getOuvertureAt(),
                sessionEnregistree.getExpirationAt()
        );
    }

    private String genererCodeUnique() {

        String code;

        do {
            code = genererCode();
        } while (courseSessionRepository.existsByCode(code));

        return code;
    }

    private String genererCode() {

        StringBuilder code = new StringBuilder(LONGUEUR_CODE);

        for (int i = 0; i < LONGUEUR_CODE; i++) {
            int index = secureRandom.nextInt(CARACTERES.length());
            code.append(CARACTERES.charAt(index));
        }

        return code.toString();
    }
}