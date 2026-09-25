package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.SessionResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.exception.BusinessException;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SessionWorkflowService {
    private final CourseSessionRepository sessionRepository;
    private final PromotionRepository promotionRepository;
    private final Clock clock;

    public SessionWorkflowService(CourseSessionRepository sessionRepository, PromotionRepository promotionRepository, Clock clock) {
        this.sessionRepository = sessionRepository;
        this.promotionRepository = promotionRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> lister(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new BusinessException("PROMOTION_INCONNUE", "La promotion demandée est inconnue.", HttpStatus.NOT_FOUND);
        }
        return sessionRepository.findByPromotionIdOrderByOuvertureAtDesc(promotionId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public SessionResponse terminer(Long id) {
        CourseSession session = get(id);
        if (session.getStatut() != SessionStatus.OUVERTE) {
            throw new BusinessException("SESSION_DEJA_TERMINEE", "La session n'est plus ouverte.", HttpStatus.CONFLICT);
        }
        session.setStatut(SessionStatus.TERMINEE);
        session.setFinAt(OffsetDateTime.now(clock));
        return toResponse(sessionRepository.save(session));
    }

    @Transactional
    public SessionResponse cloturer(Long id) {
        CourseSession session = get(id);
        if (session.getStatut() == SessionStatus.CLOTUREE) {
            throw new BusinessException("SESSION_DEJA_CLOTUREE", "La session est déjà clôturée.", HttpStatus.CONFLICT);
        }
        if (session.getFinAt() == null) session.setFinAt(OffsetDateTime.now(clock));
        session.setStatut(SessionStatus.CLOTUREE);
        session.setClotureAt(OffsetDateTime.now(clock));
        return toResponse(sessionRepository.save(session));
    }

    private CourseSession get(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("SESSION_INCONNUE", "La session demandée est inconnue.", HttpStatus.NOT_FOUND));
    }

    private SessionResponse toResponse(CourseSession s) {
        return new SessionResponse(s.getId(), s.getTitre(), s.getCode(), s.getStatut(), s.getOuvertureAt(), s.getExpirationAt(), s.getFinAt(), s.getClotureAt(), s.getPromotion().getId());
    }
}
