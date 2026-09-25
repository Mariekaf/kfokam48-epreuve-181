package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.CreateSessionRequest;
import com.kfokam48.backend.dto.CreateSessionResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private CourseSessionRepository courseSessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void doitOuvrirUneSessionAvecUnCodeValidePendant15Minutes() {

        Promotion promotion = new Promotion("KFOKAM48");

        when(promotionRepository.findById(1L))
                .thenReturn(Optional.of(promotion));

        when(courseSessionRepository.existsByCode(any(String.class)))
                .thenReturn(false);

        when(courseSessionRepository.save(any(CourseSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateSessionRequest request =
                new CreateSessionRequest("Séance Spring Boot", 1L);

        CreateSessionResponse response =
                sessionService.ouvrirSession(request);

        assertEquals(
                15,
                Duration.between(
                        response.ouvertureAt(),
                        response.expirationAt()
                ).toMinutes()
        );

        ArgumentCaptor<CourseSession> captor =
                ArgumentCaptor.forClass(CourseSession.class);

        verify(courseSessionRepository).save(captor.capture());

        CourseSession sessionEnregistree = captor.getValue();

        assertEquals("Séance Spring Boot", sessionEnregistree.getTitre());
        assertEquals(SessionStatus.OUVERTE, sessionEnregistree.getStatut());
        assertSame(promotion, sessionEnregistree.getPromotion());

        verify(promotionRepository).findById(1L);
        verify(courseSessionRepository).existsByCode(any(String.class));
        verify(courseSessionRepository).save(any(CourseSession.class));
    }
}