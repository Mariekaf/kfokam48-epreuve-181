package com.kfokam48.backend.controller;

import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.ExerciceStatus;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.ExerciceRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExerciceControllerIntegrationTest {

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

    @Test
    void doitRetourner201LorsDuDepotDUnExercice() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Exercice Succes")
        );
        CourseSession session = courseSessionRepository.save(
                session("EXS001", promotion, SessionStatus.OUVERTE)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Ada Lovelace", promotion)
        );

        mockMvc.perform(
                        post("/api/exercices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(
                                        session.getId(),
                                        etudiant.getId(),
                                        "https://example.com/exercices/1"
                                ))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.statut").value("DEPOSE"));
    }

    @Test
    void doitRetourner400LorsqueLeLienEstInvalide() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Lien Invalide")
        );
        CourseSession session = courseSessionRepository.save(
                session("EXL001", promotion, SessionStatus.OUVERTE)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Grace Hopper", promotion)
        );

        mockMvc.perform(
                        post("/api/exercices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(
                                        session.getId(),
                                        etudiant.getId(),
                                        "pas-un-lien"
                                ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("LIEN_INVALIDE"))
                .andExpect(jsonPath("$.message").value("Le lien de l'exercice est invalide."));
    }

    @Test
    void doitRetourner409LorsqueLExerciceExisteDeja() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Exercice Doublon")
        );
        CourseSession session = courseSessionRepository.save(
                session("EXD001", promotion, SessionStatus.OUVERTE)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Alan Turing", promotion)
        );

        exerciceRepository.save(
                exercice(session, etudiant, "https://example.com/deja")
        );

        mockMvc.perform(
                        post("/api/exercices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(
                                        session.getId(),
                                        etudiant.getId(),
                                        "https://example.com/nouveau"
                                ))
                )
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("EXERCICE_DEJA_DEPOSE"));
    }

    @Test
    void doitRetourner400LorsqueLaSessionEstInconnue() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Session Inconnue")
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Katherine Johnson", promotion)
        );

        mockMvc.perform(
                        post("/api/exercices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(
                                        999999L,
                                        etudiant.getId(),
                                        "https://example.com/exercice"
                                ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("SESSION_INCONNUE"));
    }

    @Test
    void doitRetourner400LorsqueLEtudiantEstInconnu() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Etudiant Inconnu")
        );
        CourseSession session = courseSessionRepository.save(
                session("EXE001", promotion, SessionStatus.OUVERTE)
        );

        mockMvc.perform(
                        post("/api/exercices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(
                                        session.getId(),
                                        999999L,
                                        "https://example.com/exercice"
                                ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("ETUDIANT_INCONNU"));
    }

    @Test
    void doitRetourner409LorsqueLaSessionEstCloturee() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Session Cloturee")
        );
        CourseSession session = courseSessionRepository.save(
                session("EXC001", promotion, SessionStatus.CLOTUREE)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Mary Jackson", promotion)
        );

        mockMvc.perform(
                        post("/api/exercices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(
                                        session.getId(),
                                        etudiant.getId(),
                                        "https://example.com/exercice"
                                ))
                )
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("SESSION_CLOTUREE"));
    }

    @Test
    void doitAutoriserLeDepotPourUneSessionTermineeNonCloturee()
            throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Session Terminee")
        );
        CourseSession session = courseSessionRepository.save(
                session("EXT001", promotion, SessionStatus.TERMINEE)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Dorothy Vaughan", promotion)
        );

        mockMvc.perform(
                        post("/api/exercices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body(
                                        session.getId(),
                                        etudiant.getId(),
                                        "https://example.com/exercice-termine"
                                ))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("DEPOSE"));
    }

    private String body(Long sessionId, Long etudiantId, String lien) {

        return """
                {
                  "sessionId": %d,
                  "etudiantId": %d,
                  "lien": "%s"
                }
                """.formatted(sessionId, etudiantId, lien);
    }

    private CourseSession session(
            String code,
            Promotion promotion,
            SessionStatus statut
    ) {

        OffsetDateTime maintenant = OffsetDateTime.now();

        CourseSession session = new CourseSession();
        session.setTitre("Seance Exercice");
        session.setCode(code);
        session.setOuvertureAt(maintenant.minusMinutes(30));
        session.setExpirationAt(maintenant.minusMinutes(15));
        session.setStatut(statut);
        session.setPromotion(promotion);

        if (SessionStatus.TERMINEE.equals(statut)) {
            session.setFinAt(maintenant.minusMinutes(5));
        }

        if (SessionStatus.CLOTUREE.equals(statut)) {
            session.setFinAt(maintenant.minusMinutes(10));
            session.setClotureAt(maintenant.minusMinutes(5));
        }

        return session;
    }

    private Exercice exercice(
            CourseSession session,
            Etudiant etudiant,
            String lien
    ) {

        Exercice exercice = new Exercice();
        exercice.setLien(lien);
        exercice.setStatut(ExerciceStatus.DEPOSE);
        exercice.setDeposeAt(OffsetDateTime.now());
        exercice.setSession(session);
        exercice.setEtudiant(etudiant);

        return exercice;
    }
}
