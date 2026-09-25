package com.kfokam48.backend.controller;

import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.repository.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PromotionRepository promotionRepository;

    @Test
    void doitRetourner201LorsDeLOuvertureDUneSession() throws Exception {

        Promotion promotion =
                promotionRepository.save(new Promotion("KFOKAM48 Integration"));

        String body = """
                {
                  "titre": "Séance Spring Boot",
                  "promotionId": %d
                }
                """.formatted(promotion.getId());

        mockMvc.perform(
                        post("/api/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").isString())
                .andExpect(jsonPath("$.ouvertureAt").exists())
                .andExpect(jsonPath("$.expirationAt").exists());
    }

    @Test
    void doitRetourner400LorsqueLeTitreEstAbsent() throws Exception {

        String body = """
                {
                  "promotionId": 1
                }
                """;

        mockMvc.perform(
                        post("/api/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Le titre est obligatoire."));
    }
}