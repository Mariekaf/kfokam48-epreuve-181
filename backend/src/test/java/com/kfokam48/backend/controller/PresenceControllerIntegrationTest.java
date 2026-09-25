package com.kfokam48.backend.controller;

import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Presence;
import com.kfokam48.backend.entity.PresenceSource;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.SessionStatus;
import com.kfokam48.backend.entity.TentativePresence;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.PresenceRepository;
import com.kfokam48.backend.repository.PromotionRepository;
import com.kfokam48.backend.repository.TentativePresenceRepository;
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

    @Autowired
    private TentativePresenceRepository tentativePresenceRepository;

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

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Code Inconnu")
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Katherine Johnson", promotion)
        );

        String body = """
                {
                  "code": "ABSENT",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("CODE_INCONNU"))
                .andExpect(jsonPath("$.message").value("Le code de session est inconnu."));

        TentativePresence tentativePresence = tentativePresenceRepository
                .findByEtudiant(etudiant)
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                1,
                tentativePresence.getNombreTentativesIncorrectes()
        );
        org.junit.jupiter.api.Assertions.assertNull(
                tentativePresence.getBloqueJusqua()
        );
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

    @Test
    void doitNePasBloquerAvantLaCinquiemeTentativeIncorrecte()
            throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Quatre Tentatives")
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Margaret Hamilton", promotion)
        );

        for (int i = 1; i <= 4; i++) {
            mauvaiseTentative(etudiant, "BADQ" + i)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("CODE_INCONNU"));
        }

        TentativePresence tentativePresence = tentativePresenceRepository
                .findByEtudiant(etudiant)
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                4,
                tentativePresence.getNombreTentativesIncorrectes()
        );
        org.junit.jupiter.api.Assertions.assertNull(
                tentativePresence.getBloqueJusqua()
        );
    }

    @Test
    void doitBloquerALaCinquiemeTentativeIncorrecte() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Cinquieme Tentative")
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Mary Jackson", promotion)
        );

        for (int i = 1; i <= 4; i++) {
            mauvaiseTentative(etudiant, "BADC" + i)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("CODE_INCONNU"));
        }

        mauvaiseTentative(etudiant, "BADC5")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TENTATIVES_BLOQUEES"))
                .andExpect(jsonPath("$.message").value(
                        "Trop de tentatives incorrectes. Réessayez dans 2 minutes."
                ));

        TentativePresence tentativePresence = tentativePresenceRepository
                .findByEtudiant(etudiant)
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                5,
                tentativePresence.getNombreTentativesIncorrectes()
        );
        org.junit.jupiter.api.Assertions.assertNotNull(
                tentativePresence.getBloqueJusqua()
        );
    }

    @Test
    void doitRefuserUnCodeCorrectPendantLeBlocage() throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Blocage Actif")
        );
        courseSessionRepository.save(session("BLK001", promotion, 15));
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Dorothy Vaughan", promotion)
        );

        TentativePresence tentativePresence =
                new TentativePresence(etudiant, OffsetDateTime.now());
        tentativePresence.setNombreTentativesIncorrectes(5);
        tentativePresence.setBloqueJusqua(OffsetDateTime.now().plusMinutes(1));
        tentativePresenceRepository.save(tentativePresence);

        String body = """
                {
                  "code": "BLK001",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TENTATIVES_BLOQUEES"));
    }

    @Test
    void doitAutoriserUneNouvelleTentativeApresExpirationDuBlocage()
            throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Blocage Expire")
        );
        CourseSession session = courseSessionRepository.save(
                session("UNBLK1", promotion, 15)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Joan Clarke", promotion)
        );

        TentativePresence tentativePresence =
                new TentativePresence(etudiant, OffsetDateTime.now());
        tentativePresence.setNombreTentativesIncorrectes(5);
        tentativePresence.setBloqueJusqua(OffsetDateTime.now().minusMinutes(1));
        tentativePresenceRepository.save(tentativePresence);

        String body = """
                {
                  "code": "UNBLK1",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value(session.getId()))
                .andExpect(jsonPath("$.etudiantId").value(etudiant.getId()));
    }

    @Test
    void doitRemettreLeCompteurAZeroApresUnePresenceEnregistree()
            throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Reset Succes")
        );
        courseSessionRepository.save(session("RESET1", promotion, 15));
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Radia Perlman", promotion)
        );

        TentativePresence tentativePresence =
                new TentativePresence(etudiant, OffsetDateTime.now());
        tentativePresence.setNombreTentativesIncorrectes(3);
        tentativePresenceRepository.save(tentativePresence);

        String body = """
                {
                  "code": "RESET1",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated());

        TentativePresence tentativeRechargee = tentativePresenceRepository
                .findByEtudiant(etudiant)
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                tentativeRechargee.getNombreTentativesIncorrectes()
        );
        org.junit.jupiter.api.Assertions.assertNull(
                tentativeRechargee.getBloqueJusqua()
        );
    }

    @Test
    void doitIgnorerUnCodeExpireDansLeCompteurDesMauvaisCodes()
            throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Expire Non Compte")
        );
        courseSessionRepository.save(session("OLD001", promotion, -1));
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Hedy Lamarr", promotion)
        );

        TentativePresence tentativePresence =
                new TentativePresence(etudiant, OffsetDateTime.now());
        tentativePresence.setNombreTentativesIncorrectes(2);
        tentativePresenceRepository.save(tentativePresence);

        String body = """
                {
                  "code": "OLD001",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.code").value("CODE_EXPIRE"));

        TentativePresence tentativeRechargee = tentativePresenceRepository
                .findByEtudiant(etudiant)
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                2,
                tentativeRechargee.getNombreTentativesIncorrectes()
        );
    }

    @Test
    void doitIgnorerUnePresenceDejaExistanteDansLeCompteurDesMauvaisCodes()
            throws Exception {

        Promotion promotion = promotionRepository.save(
                new Promotion("KFOKAM48 Deja Present Non Compte")
        );
        CourseSession session = courseSessionRepository.save(
                session("DPC001", promotion, 15)
        );
        Etudiant etudiant = etudiantRepository.save(
                new Etudiant("Annie Easley", promotion)
        );

        TentativePresence tentativePresence =
                new TentativePresence(etudiant, OffsetDateTime.now());
        tentativePresence.setNombreTentativesIncorrectes(2);
        tentativePresenceRepository.save(tentativePresence);

        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(PresenceSource.ETUDIANT);
        presence.setEnregistreeAt(OffsetDateTime.now());
        presenceRepository.save(presence);

        String body = """
                {
                  "code": "DPC001",
                  "etudiantId": %d
                }
                """.formatted(etudiant.getId());

        mockMvc.perform(
                        post("/api/presences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEJA_PRESENT"));

        TentativePresence tentativeRechargee = tentativePresenceRepository
                .findByEtudiant(etudiant)
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                2,
                tentativeRechargee.getNombreTentativesIncorrectes()
        );
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

    private org.springframework.test.web.servlet.ResultActions mauvaiseTentative(
            Etudiant etudiant,
            String code
    ) throws Exception {

        String body = """
                {
                  "code": "%s",
                  "etudiantId": %d
                }
                """.formatted(code, etudiant.getId());

        return mockMvc.perform(
                post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );
    }
}
