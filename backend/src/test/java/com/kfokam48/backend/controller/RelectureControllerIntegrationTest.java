package com.kfokam48.backend.controller;

import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.ExerciceStatus;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.Relecture;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.ExerciceRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import com.kfokam48.backend.repository.RelectureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RelectureControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private CourseSessionRepository courseSessionRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private ExerciceRepository exerciceRepository;

    @Autowired
    private RelectureRepository relectureRepository;

    @Test
    void doitRetourner200LorsDUneSoumissionValide() throws Exception {

        Relecture relecture = relecture(RelectureStatus.A_FAIRE);

        mockMvc.perform(
                        post("/api/relectures/{id}", relecture.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "note": 15,
                                          "commentaire": "Travail correct et bien structure."
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        Relecture relectureRechargee = relectureRepository
                .findById(relecture.getId())
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                15,
                relectureRechargee.getNote()
        );
        org.junit.jupiter.api.Assertions.assertEquals(
                "Travail correct et bien structure.",
                relectureRechargee.getCommentaire()
        );
        org.junit.jupiter.api.Assertions.assertEquals(
                RelectureStatus.RELU,
                relectureRechargee.getStatut()
        );
        org.junit.jupiter.api.Assertions.assertNotNull(
                relectureRechargee.getRendueAt()
        );
    }

    @Test
    void doitRetourner400LorsqueLaNoteEstNegative() throws Exception {

        Relecture relecture = relecture(RelectureStatus.A_FAIRE);

        mockMvc.perform(
                        post("/api/relectures/{id}", relecture.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(-1))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NOTE_INVALIDE"));
    }

    @Test
    void doitRetourner400LorsqueLaNoteEstSuperieureAVingt() throws Exception {

        Relecture relecture = relecture(RelectureStatus.A_FAIRE);

        mockMvc.perform(
                        post("/api/relectures/{id}", relecture.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(21))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NOTE_INVALIDE"));
    }

    @Test
    void doitRetourner400LorsqueLaNoteEstDecimale() throws Exception {

        Relecture relecture = relecture(RelectureStatus.A_FAIRE);

        mockMvc.perform(
                        post("/api/relectures/{id}", relecture.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "note": 15.5,
                                          "commentaire": "Commentaire"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NOTE_INVALIDE"));
    }

    @Test
    void doitRetourner404LorsqueLaRelectureEstInconnue() throws Exception {

        mockMvc.perform(
                        post("/api/relectures/{id}", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(12))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RELECTURE_INCONNUE"));
    }

    @Test
    void doitRetourner403LorsDUneAutoRelecture() throws Exception {

        Relecture relecture = relecture(RelectureStatus.A_FAIRE);
        relecture.setRelecteur(relecture.getExercice().getEtudiant());
        relectureRepository.save(relecture);

        mockMvc.perform(
                        post("/api/relectures/{id}", relecture.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(12))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTO_RELECTURE"));
    }

    @Test
    void doitRetourner409LorsqueLaRelectureEstDejaRendue() throws Exception {

        Relecture relecture = relecture(RelectureStatus.RELU);
        relecture.setNote(14);
        relecture.setCommentaire("Deja rendu");
        relecture.setRendueAt(OffsetDateTime.now());
        relectureRepository.save(relecture);

        mockMvc.perform(
                        post("/api/relectures/{id}", relecture.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(12))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RELECTURE_DEJA_RENDUE"));
    }

    private String body(int note) {

        return """
                {
                  "note": %d,
                  "commentaire": "Commentaire"
                }
                """.formatted(note);
    }

    private Relecture relecture(RelectureStatus statut) {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Relecture " + System.nanoTime())
        );

        CourseSession session = courseSessionRepository.save(
                session(promotion)
        );

        Etudiant auteur = etudiantRepository.save(
                new Etudiant("Auteur " + System.nanoTime(), promotion)
        );
        Etudiant relecteur = etudiantRepository.save(
                new Etudiant("Relecteur " + System.nanoTime(), promotion)
        );

        Exercice exercice = new Exercice();
        exercice.setLien("https://example.com/exercice-" + System.nanoTime());
        exercice.setStatut(ExerciceStatus.DEPOSE);
        exercice.setDeposeAt(OffsetDateTime.now());
        exercice.setSession(session);
        exercice.setEtudiant(auteur);
        Exercice exerciceEnregistre = exerciceRepository.save(exercice);

        Relecture relecture = new Relecture();
        relecture.setExercice(exerciceEnregistre);
        relecture.setRelecteur(relecteur);
        relecture.setStatut(statut);
        relecture.setAffecteeAt(OffsetDateTime.now());

        return relectureRepository.save(relecture);
    }

    private CourseSession session(Promotion promotion) {

        OffsetDateTime maintenant = OffsetDateTime.now();

        CourseSession session = new CourseSession();
        session.setTitre("Seance Relecture");
        session.setCode("R" + System.nanoTime());
        session.setOuvertureAt(maintenant.minusMinutes(30));
        session.setExpirationAt(maintenant.minusMinutes(15));
        session.setStatut(SessionStatus.OUVERTE);
        session.setPromotion(promotion);

        return session;
    }
}
