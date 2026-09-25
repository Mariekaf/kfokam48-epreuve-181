package com.kfokam48.backend.controller;

import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Presence;
import com.kfokam48.backend.entity.PresenceSource;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.PresenceRepository;
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
class PresenceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private CourseSessionRepository courseSessionRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    @Test
    void doitRetourner201LorsqueLaPresenceEstMarquee() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Presence Succes")
        );
        CourseSession session = courseSessionRepository.save(
                session("PRES01", promotion, 15)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Ada Lovelace", promotion)
        );

        String body = """
                {
                  "code": "PRES01",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.sessionId").value(session.getId()))
                .andExpect(jsonPath("$.etudiantId").value(etudiant.getId()))
                .andExpect(jsonPath("$.source").value("ETUDIANT"));
    }

    @Test
    void doitRetourner400LorsqueLeCodeEstInconnu() throws Exception {

        String body = """
                {
                  "code": "ABSENT",
                  "etudiantId": 1
                }
                """;

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("CODE_INCONNU"))
                .andExpect(jsonPath("$.message").value("Le code de session est inconnu."));
    }

    @Test
    void doitRetourner410LorsqueLeCodeEstExpire() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Presence Expiree")
        );
        courseSessionRepository.save(session("EXP001", promotion, -1));
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Grace Hopper", promotion)
        );

        String body = """
                {
                  "code": "EXP001",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isGone())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("CODE_EXPIRE"))
                .andExpect(jsonPath("$.message").value("Le code de presence a expire."));
    }

    @Test
    void doitRetourner409LorsqueLEtudiantEstDejaPresent() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Presence Doublon")
        );
        CourseSession session = courseSessionRepository.save(
                session("DUP001", promotion, 15)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Alan Turing", promotion)
        );

        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(PresenceSource.ETUDIANT);
        presence.setEnregistreeAt(OffsetDateTime.now());
        presenceRepository.save(presence);

        String body = """
                {
                  "code": "DUP001",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("DEJA_PRESENT"))
                .andExpect(jsonPath("$.message").value("L'etudiant est deja present pour cette session."));
    }

    private CourseSession session(
            String code,
            Promotion promotion,
            int minutesAvantExpiration
    ) {

        OffsetDateTime maintenant = OffsetDateTime.now();

        CourseSession session = new CourseSession();
        session.setTitre("Seance Presence");
        session.setCode(code);
        session.setOuvertureAt(maintenant.minusMinutes(5));
        session.setExpirationAt(maintenant.plusMinutes(minutesAvantExpiration));
        session.setStatut(SessionStatus.OUVERTE);
        session.setPromotion(promotion);

        return session;
    }
}
