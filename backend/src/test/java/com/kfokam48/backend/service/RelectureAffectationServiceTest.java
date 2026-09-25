package com.kfokam48.backend.service;

import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.Relecture;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.repository.PresenceRepository;
import com.kfokam48.backend.repository.RelectureRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelectureAffectationServiceTest {

    @Mock
    private PresenceRepository presenceRepository;

    @Mock
    private RelectureRepository relectureRepository;

    @Mock
    private RelecteurSelector relecteurSelector;

    private RelectureAffectationService relectureAffectationService;

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-09-25T12:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void setUp() {
        relectureAffectationService = new RelectureAffectationService(
                presenceRepository,
                relectureRepository,
                relecteurSelector,
                clock
        );
    }

    @Test
    void doitCreerUneRelecturePourUnRelecteurEligible() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, promotion);
        Etudiant auteur = etudiant(20L, promotion);
        Etudiant relecteur = etudiant(21L, promotion);
        Exercice exercice = exercice(30L, session, auteur);

        when(relectureRepository.existsByExercice(exercice))
                .thenReturn(false);
        when(presenceRepository.findEtudiantsPresentsEligibles(
                session,
                auteur
        )).thenReturn(List.of(relecteur));
        when(relecteurSelector.choisir(List.of(relecteur)))
                .thenReturn(Optional.of(relecteur));

        relectureAffectationService.affecterRelecteurSiPossible(exercice);

        ArgumentCaptor<Relecture> captor =
                ArgumentCaptor.forClass(Relecture.class);

        verify(relectureRepository).save(captor.capture());

        Relecture relecture = captor.getValue();

        assertSame(exercice, relecture.getExercice());
        assertSame(relecteur, relecture.getRelecteur());
        assertEquals(RelectureStatus.A_FAIRE, relecture.getStatut());
        assertEquals(OffsetDateTime.now(clock), relecture.getAffecteeAt());
    }

    @Test
    void neDoitPasCreerUneSecondeRelecture() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, promotion);
        Etudiant auteur = etudiant(20L, promotion);
        Exercice exercice = exercice(30L, session, auteur);

        when(relectureRepository.existsByExercice(exercice))
                .thenReturn(true);

        relectureAffectationService.affecterRelecteurSiPossible(exercice);

        verify(presenceRepository, never())
                .findEtudiantsPresentsEligibles(any(), any());
        verify(relectureRepository, never()).save(any(Relecture.class));
    }

    @Test
    void neDoitPasCreerDeRelectureSansRelecteurEligible() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, promotion);
        Etudiant auteur = etudiant(20L, promotion);
        Exercice exercice = exercice(30L, session, auteur);

        when(relectureRepository.existsByExercice(exercice))
                .thenReturn(false);
        when(presenceRepository.findEtudiantsPresentsEligibles(
                session,
                auteur
        )).thenReturn(List.of());
        when(relecteurSelector.choisir(List.of()))
                .thenReturn(Optional.empty());

        relectureAffectationService.affecterRelecteurSiPossible(exercice);

        verify(relectureRepository, never()).save(any(Relecture.class));
    }

    private Promotion promotion(Long id) {
        Promotion promotion = new Promotion("KFOKAM48");
        ReflectionTestUtils.setField(promotion, "id", id);
        return promotion;
    }

    private CourseSession session(Long id, Promotion promotion) {
        CourseSession session = new CourseSession();
        ReflectionTestUtils.setField(session, "id", id);
        session.setPromotion(promotion);
        return session;
    }

    private Etudiant etudiant(Long id, Promotion promotion) {
        Etudiant etudiant = new Etudiant("Etudiant " + id, promotion);
        ReflectionTestUtils.setField(etudiant, "id", id);
        return etudiant;
    }

    private Exercice exercice(
            Long id,
            CourseSession session,
            Etudiant auteur
    ) {
        Exercice exercice = new Exercice();
        ReflectionTestUtils.setField(exercice, "id", id);
        exercice.setSession(session);
        exercice.setEtudiant(auteur);
        return exercice;
    }
}
