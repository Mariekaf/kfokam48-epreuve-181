package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.MarkPresenceRequest;
import com.kfokam48.backend.dto.MarkPresenceResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Presence;
import com.kfokam48.backend.entity.PresenceSource;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.exception.CodeSessionExpireException;
import com.kfokam48.backend.exception.CodeSessionInconnuException;
import com.kfokam48.backend.exception.PresenceDejaEnregistreeException;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.PresenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresenceServiceTest {

    @Mock
    private CourseSessionRepository courseSessionRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private PresenceRepository presenceRepository;

    @InjectMocks
    private PresenceService presenceService;

    @Test
    void doitMarquerLaPresenceAvecLaSourceEtudiant() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, "K7M4RX", promotion, 15);
        Etudiant etudiant = etudiant(20L, promotion);

        when(courseSessionRepository.findByCode("K7M4RX"))
                .thenReturn(Optional.of(session));
        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(presenceRepository.existsBySessionAndEtudiant(session, etudiant))
                .thenReturn(false);
        when(presenceRepository.save(any(Presence.class)))
                .thenAnswer(invocation -> {
                    Presence presence = invocation.getArgument(0);
                    ReflectionTestUtils.setField(presence, "id", 30L);
                    return presence;
                });

        MarkPresenceResponse response = presenceService.marquerPresence(
                new MarkPresenceRequest("K7M4RX", 20L)
        );

        assertEquals(30L, response.id());
        assertEquals(10L, response.sessionId());
        assertEquals(20L, response.etudiantId());
        assertEquals(PresenceSource.ETUDIANT, response.source());

        ArgumentCaptor<Presence> captor =
                ArgumentCaptor.forClass(Presence.class);

        verify(presenceRepository).save(captor.capture());

        Presence presenceEnregistree = captor.getValue();

        assertSame(session, presenceEnregistree.getSession());
        assertSame(etudiant, presenceEnregistree.getEtudiant());
        assertEquals(PresenceSource.ETUDIANT, presenceEnregistree.getSource());
    }

    @Test
    void doitRefuserUnCodeInconnu() {

        when(courseSessionRepository.findByCode("INCONNU"))
                .thenReturn(Optional.empty());

        assertThrows(
                CodeSessionInconnuException.class,
                () -> presenceService.marquerPresence(
                        new MarkPresenceRequest("INCONNU", 20L)
                )
        );

        verify(presenceRepository, never()).save(any(Presence.class));
    }

    @Test
    void doitRefuserUnCodeExpire() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, "EXPIRE", promotion, -1);

        when(courseSessionRepository.findByCode("EXPIRE"))
                .thenReturn(Optional.of(session));

        assertThrows(
                CodeSessionExpireException.class,
                () -> presenceService.marquerPresence(
                        new MarkPresenceRequest("EXPIRE", 20L)
                )
        );

        verify(etudiantRepository, never()).findById(20L);
        verify(presenceRepository, never()).save(any(Presence.class));
    }

    @Test
    void doitRefuserUnePresenceDejaEnregistree() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, "K7M4RX", promotion, 15);
        Etudiant etudiant = etudiant(20L, promotion);

        when(courseSessionRepository.findByCode("K7M4RX"))
                .thenReturn(Optional.of(session));
        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(presenceRepository.existsBySessionAndEtudiant(session, etudiant))
                .thenReturn(true);

        assertThrows(
                PresenceDejaEnregistreeException.class,
                () -> presenceService.marquerPresence(
                        new MarkPresenceRequest("K7M4RX", 20L)
                )
        );

        verify(presenceRepository, never()).save(any(Presence.class));
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
            String code,
            Promotion promotion,
            int minutesAvantExpiration
    ) {

        CourseSession session = new CourseSession();
        ReflectionTestUtils.setField(session, "id", id);
        session.setTitre("Seance Spring Boot");
        session.setCode(code);
        session.setOuvertureAt(OffsetDateTime.now().minusMinutes(5));
        session.setExpirationAt(
                OffsetDateTime.now().plusMinutes(minutesAvantExpiration)
        );
        session.setPromotion(promotion);

        return session;
    }
}
