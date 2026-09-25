package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.CreateExerciceRequest;
import com.kfokam48.backend.dto.CreateExerciceResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.ExerciceStatus;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.exception.ExerciceDejaDeposeException;
import com.kfokam48.backend.exception.LienInvalideException;
import com.kfokam48.backend.exception.SessionClotureeException;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.ExerciceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciceServiceTest {

    @Mock
    private CourseSessionRepository courseSessionRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ExerciceRepository exerciceRepository;

    private ExerciceService exerciceService;

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-09-25T12:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void setUp() {
        exerciceService = new ExerciceService(
                courseSessionRepository,
                etudiantRepository,
                exerciceRepository,
                clock
        );
    }

    @Test
    void doitDeposerUnExerciceAvecLeStatutDepose() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, promotion, SessionStatus.OUVERTE);
        Etudiant etudiant = etudiant(20L, promotion);

        when(courseSessionRepository.findById(10L))
                .thenReturn(Optional.of(session));
        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(exerciceRepository.existsBySessionAndEtudiant(session, etudiant))
                .thenReturn(false);
        when(exerciceRepository.save(any(Exercice.class)))
                .thenAnswer(invocation -> {
                    Exercice exercice = invocation.getArgument(0);
                    ReflectionTestUtils.setField(exercice, "id", 30L);
                    return exercice;
                });

        CreateExerciceResponse response = exerciceService.deposerExercice(
                new CreateExerciceRequest(
                        10L,
                        20L,
                        " https://example.com/exercice "
                )
        );

        assertEquals(30L, response.id());
        assertEquals(ExerciceStatus.DEPOSE, response.statut());

        ArgumentCaptor<Exercice> captor =
                ArgumentCaptor.forClass(Exercice.class);

        verify(exerciceRepository).save(captor.capture());

        Exercice exercice = captor.getValue();

        assertEquals("https://example.com/exercice", exercice.getLien());
        assertEquals(ExerciceStatus.DEPOSE, exercice.getStatut());
        assertEquals(OffsetDateTime.now(clock), exercice.getDeposeAt());
        assertSame(session, exercice.getSession());
        assertSame(etudiant, exercice.getEtudiant());
    }

    @Test
    void doitRefuserUnLienInvalide() {

        assertThrows(
                LienInvalideException.class,
                () -> exerciceService.deposerExercice(
                        new CreateExerciceRequest(10L, 20L, "ftp://example.com")
                )
        );

        verify(exerciceRepository, never()).save(any(Exercice.class));
    }

    @Test
    void doitRefuserUnExerciceDejaDepose() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, promotion, SessionStatus.OUVERTE);
        Etudiant etudiant = etudiant(20L, promotion);

        when(courseSessionRepository.findById(10L))
                .thenReturn(Optional.of(session));
        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(exerciceRepository.existsBySessionAndEtudiant(session, etudiant))
                .thenReturn(true);

        assertThrows(
                ExerciceDejaDeposeException.class,
                () -> exerciceService.deposerExercice(
                        new CreateExerciceRequest(
                                10L,
                                20L,
                                "https://example.com/exercice"
                        )
                )
        );

        verify(exerciceRepository, never()).save(any(Exercice.class));
    }

    @Test
    void doitRefuserUneSessionCloturee() {

        Promotion promotion = promotion(1L);
        CourseSession session =
                session(10L, promotion, SessionStatus.CLOTUREE);
        Etudiant etudiant = etudiant(20L, promotion);

        when(courseSessionRepository.findById(10L))
                .thenReturn(Optional.of(session));
        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));

        assertThrows(
                SessionClotureeException.class,
                () -> exerciceService.deposerExercice(
                        new CreateExerciceRequest(
                                10L,
                                20L,
                                "https://example.com/exercice"
                        )
                )
        );

        verify(exerciceRepository, never()).save(any(Exercice.class));
    }

    private Promotion promotion(Long id) {

        Promotion promotion = new Promotion("KFOKAM48");
        ReflectionTestUtils.setField(promotion, "id", id);

        return promotion;
    }

    private Etudiant etudiant(Long id, Promotion promotion) {

        Etudiant etudiant = new Etudiant("Ada Lovelace", promotion);
        ReflectionTestUtils.setField(etudiant, "id", id);

        return etudiant;
    }

    private CourseSession session(
            Long id,
            Promotion promotion,
            SessionStatus statut
    ) {

        CourseSession session = new CourseSession();
        ReflectionTestUtils.setField(session, "id", id);
        session.setTitre("Seance Spring Boot");
        session.setCode("EX" + id);
        session.setOuvertureAt(OffsetDateTime.now(clock).minusMinutes(30));
        session.setExpirationAt(OffsetDateTime.now(clock).minusMinutes(15));
        session.setStatut(statut);
        session.setPromotion(promotion);

        return session;
    }
}
