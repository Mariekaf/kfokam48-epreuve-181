package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.TableauEtudiantResponse;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.exception.PromotionTableauInconnueException;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.ExerciceRepository;
import com.kfokam48.backend.repository.PresenceRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import com.kfokam48.backend.repository.RelectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableauServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private PresenceRepository presenceRepository;

    @Mock
    private ExerciceRepository exerciceRepository;

    @Mock
    private RelectureRepository relectureRepository;

    @InjectMocks
    private TableauService tableauService;

    @Test
    void doitConstruireLeTableauAvecLesIndicateurs() {

        Promotion promotion = new Promotion("KFOKAM48");
        Etudiant etudiant = new Etudiant("Marie", promotion);

        when(promotionRepository.existsById(1L))
                .thenReturn(true);

        when(etudiantRepository.findByPromotionIdOrderByNomAsc(1L))
                .thenReturn(List.of(etudiant));

        when(presenceRepository.countByEtudiant(etudiant))
                .thenReturn(3L);

        when(exerciceRepository.countByEtudiant(etudiant))
                .thenReturn(2L);

        when(relectureRepository.calculerMoyenneNotesRecues(
                etudiant,
                RelectureStatus.RELU
        )).thenReturn(15.0);

        when(relectureRepository.countByRelecteurAndStatutIn(
                eq(etudiant),
                anyCollection()
        )).thenReturn(1L);

        List<TableauEtudiantResponse> resultat =
                tableauService.consulterTableau(1L);

        assertEquals(1, resultat.size());

        TableauEtudiantResponse ligne = resultat.get(0);

        assertEquals("Marie", ligne.nom());
        assertEquals(3L, ligne.presences());
        assertEquals(2L, ligne.exercicesDeposes());
        assertEquals(15.0, ligne.moyenne());
        assertEquals(1L, ligne.relecturesEnAttente());
    }

    @Test
    void doitRetournerUneMoyenneNulleSiAucuneNoteExiste() {

        Promotion promotion = new Promotion("KFOKAM48");
        Etudiant etudiant = new Etudiant("Marie", promotion);

        when(promotionRepository.existsById(1L))
                .thenReturn(true);

        when(etudiantRepository.findByPromotionIdOrderByNomAsc(1L))
                .thenReturn(List.of(etudiant));

        when(presenceRepository.countByEtudiant(etudiant))
                .thenReturn(0L);

        when(exerciceRepository.countByEtudiant(etudiant))
                .thenReturn(0L);

        when(relectureRepository.calculerMoyenneNotesRecues(
                etudiant,
                RelectureStatus.RELU
        )).thenReturn(null);

        when(relectureRepository.countByRelecteurAndStatutIn(
                eq(etudiant),
                anyCollection()
        )).thenReturn(0L);

        TableauEtudiantResponse ligne =
                tableauService.consulterTableau(1L).get(0);

        assertNull(ligne.moyenne());
        assertEquals(0L, ligne.presences());
        assertEquals(0L, ligne.exercicesDeposes());
        assertEquals(0L, ligne.relecturesEnAttente());
    }

    @Test
    void doitLeverUneErreurSiLaPromotionEstInconnue() {

        when(promotionRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                PromotionTableauInconnueException.class,
                () -> tableauService.consulterTableau(999L)
        );

        verifyNoInteractions(
                etudiantRepository,
                presenceRepository,
                exerciceRepository,
                relectureRepository
        );
    }
}