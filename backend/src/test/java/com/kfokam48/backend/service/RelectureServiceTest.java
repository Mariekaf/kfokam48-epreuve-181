package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.SubmitRelectureRequest;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.Relecture;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.exception.AutoRelectureException;
import com.kfokam48.backend.exception.NoteInvalideException;
import com.kfokam48.backend.exception.RelectureDejaRendueException;
import com.kfokam48.backend.exception.RelectureInconnueException;
import com.kfokam48.backend.repository.RelectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelectureServiceTest {

    @Mock
    private RelectureRepository relectureRepository;

    private RelectureService relectureService;

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-09-25T12:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void setUp() {
        relectureService = new RelectureService(relectureRepository, clock);
    }

    @Test
    void doitRendreUneRelectureValide() {

        Relecture relecture = relecture(1L, RelectureStatus.A_FAIRE);

        when(relectureRepository.findById(1L))
                .thenReturn(Optional.of(relecture));

        relectureService.rendreRelecture(
                1L,
                new SubmitRelectureRequest(15, " Travail correct ")
        );

        assertEquals(15, relecture.getNote());
        assertEquals("Travail correct", relecture.getCommentaire());
        assertEquals(RelectureStatus.RELU, relecture.getStatut());
        assertEquals(OffsetDateTime.now(clock), relecture.getRendueAt());
        verify(relectureRepository).save(relecture);
    }

    @Test
    void doitAccepterLaNoteZero() {

        Relecture relecture = relecture(1L, RelectureStatus.A_FAIRE);

        when(relectureRepository.findById(1L))
                .thenReturn(Optional.of(relecture));

        relectureService.rendreRelecture(
                1L,
                new SubmitRelectureRequest(0, "Minimum")
        );

        assertEquals(0, relecture.getNote());
        assertEquals(RelectureStatus.RELU, relecture.getStatut());
    }

    @Test
    void doitAccepterLaNoteVingt() {

        Relecture relecture = relecture(1L, RelectureStatus.A_FAIRE);

        when(relectureRepository.findById(1L))
                .thenReturn(Optional.of(relecture));

        relectureService.rendreRelecture(
                1L,
                new SubmitRelectureRequest(20, "Excellent")
        );

        assertEquals(20, relecture.getNote());
        assertEquals(RelectureStatus.RELU, relecture.getStatut());
    }

    @Test
    void doitRefuserUneNoteNegative() {

        assertThrows(
                NoteInvalideException.class,
                () -> relectureService.rendreRelecture(
                        1L,
                        new SubmitRelectureRequest(-1, "Impossible")
                )
        );

        verify(relectureRepository, never()).save(any(Relecture.class));
    }

    @Test
    void doitRefuserUneNoteSuperieureAVingt() {

        assertThrows(
                NoteInvalideException.class,
                () -> relectureService.rendreRelecture(
                        1L,
                        new SubmitRelectureRequest(21, "Impossible")
                )
        );

        verify(relectureRepository, never()).save(any(Relecture.class));
    }

    @Test
    void doitRefuserUneRelectureInconnue() {

        when(relectureRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RelectureInconnueException.class,
                () -> relectureService.rendreRelecture(
                        999L,
                        new SubmitRelectureRequest(12, "Commentaire")
                )
        );
    }

    @Test
    void doitRefuserUneAutoRelecture() {

        Relecture relecture = relecture(1L, RelectureStatus.A_FAIRE);
        relecture.setRelecteur(relecture.getExercice().getEtudiant());

        when(relectureRepository.findById(1L))
                .thenReturn(Optional.of(relecture));

        assertThrows(
                AutoRelectureException.class,
                () -> relectureService.rendreRelecture(
                        1L,
                        new SubmitRelectureRequest(12, "Commentaire")
                )
        );

        verify(relectureRepository, never()).save(any(Relecture.class));
    }

    @Test
    void doitRefuserUneRelectureDejaRendue() {

        Relecture relecture = relecture(1L, RelectureStatus.RELU);

        when(relectureRepository.findById(1L))
                .thenReturn(Optional.of(relecture));

        assertThrows(
                RelectureDejaRendueException.class,
                () -> relectureService.rendreRelecture(
                        1L,
                        new SubmitRelectureRequest(12, "Commentaire")
                )
        );

        verify(relectureRepository, never()).save(any(Relecture.class));
    }

    private Relecture relecture(Long id, RelectureStatus statut) {

        Promotion promotion = new Promotion("KFOKAM48");
        ReflectionTestUtils.setField(promotion, "id", 1L);

        CourseSession session = new CourseSession();
        ReflectionTestUtils.setField(session, "id", 10L);
        session.setPromotion(promotion);

        Etudiant auteur = new Etudiant("Auteur", promotion);
        ReflectionTestUtils.setField(auteur, "id", 20L);

        Etudiant relecteur = new Etudiant("Relecteur", promotion);
        ReflectionTestUtils.setField(relecteur, "id", 21L);

        Exercice exercice = new Exercice();
        ReflectionTestUtils.setField(exercice, "id", 30L);
        exercice.setSession(session);
        exercice.setEtudiant(auteur);

        Relecture relecture = new Relecture();
        ReflectionTestUtils.setField(relecture, "id", id);
        relecture.setExercice(exercice);
        relecture.setRelecteur(relecteur);
        relecture.setStatut(statut);
        relecture.setAffecteeAt(OffsetDateTime.now(clock).minusMinutes(10));

        return relecture;
    }
}
