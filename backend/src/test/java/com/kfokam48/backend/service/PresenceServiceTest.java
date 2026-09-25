package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.MarkPresenceRequest;
import com.kfokam48.backend.dto.MarkPresenceResponse;
import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Presence;
import com.kfokam48.backend.entity.PresenceSource;
import com.kfokam48.backend.entity.Promotion;
import com.kfokam48.backend.entity.TentativePresence;
import com.kfokam48.backend.exception.CodeSessionExpireException;
import com.kfokam48.backend.exception.CodeSessionInconnuException;
import com.kfokam48.backend.exception.PresenceDejaEnregistreeException;
import com.kfokam48.backend.exception.TentativesBloqueesException;
import com.kfokam48.backend.repository.CourseSessionRepository;
import com.kfokam48.backend.repository.EtudiantRepository;
import com.kfokam48.backend.repository.PresenceRepository;
import com.kfokam48.backend.repository.TentativePresenceRepository;
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
class PresenceServiceTest {

    @Mock
    private CourseSessionRepository courseSessionRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private PresenceRepository presenceRepository;

    @Mock
    private TentativePresenceRepository tentativePresenceRepository;

    private PresenceService presenceService;

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-09-25T12:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void setUp() {
        presenceService = new PresenceService(
                courseSessionRepository,
                etudiantRepository,
                presenceRepository,
                tentativePresenceRepository,
                clock
        );
    }

    @Test
    void doitMarquerLaPresenceAvecLaSourceEtudiant() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, "K7M4RX", promotion, 15);
        Etudiant etudiant = etudiant(20L, promotion);

        when(courseSessionRepository.findByCode("K7M4RX"))
                .thenReturn(Optional.of(session));
        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(tentativePresenceRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());
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

        ArgumentCaptor<TentativePresence> tentativeCaptor =
                ArgumentCaptor.forClass(TentativePresence.class);

        verify(tentativePresenceRepository).save(tentativeCaptor.capture());

        TentativePresence tentativeEnregistree = tentativeCaptor.getValue();

        assertEquals(0, tentativeEnregistree.getNombreTentativesIncorrectes());
        assertEquals(null, tentativeEnregistree.getBloqueJusqua());
    }

    @Test
    void doitRefuserUnCodeInconnu() {

        Promotion promotion = promotion(1L);
        Etudiant etudiant = etudiant(20L, promotion);

        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(tentativePresenceRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());
        when(courseSessionRepository.findByCode("INCONNU"))
                .thenReturn(Optional.empty());

        assertThrows(
                CodeSessionInconnuException.class,
                () -> presenceService.marquerPresence(
                        new MarkPresenceRequest("INCONNU", 20L)
                )
        );

        ArgumentCaptor<TentativePresence> tentativeCaptor =
                ArgumentCaptor.forClass(TentativePresence.class);

        verify(tentativePresenceRepository).save(tentativeCaptor.capture());

        assertEquals(
                1,
                tentativeCaptor.getValue().getNombreTentativesIncorrectes()
        );
        assertEquals(null, tentativeCaptor.getValue().getBloqueJusqua());
        verify(presenceRepository, never()).save(any(Presence.class));
    }

    @Test
    void doitRefuserUnCodeExpire() {

        Promotion promotion = promotion(1L);
        CourseSession session = session(10L, "EXPIRE", promotion, -1);
        Etudiant etudiant = etudiant(20L, promotion);

        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(tentativePresenceRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());
        when(courseSessionRepository.findByCode("EXPIRE"))
                .thenReturn(Optional.of(session));

        assertThrows(
                CodeSessionExpireException.class,
                () -> presenceService.marquerPresence(
                        new MarkPresenceRequest("EXPIRE", 20L)
                )
        );

        verify(tentativePresenceRepository, never())
                .save(any(TentativePresence.class));
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
        when(tentativePresenceRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());
        when(presenceRepository.existsBySessionAndEtudiant(session, etudiant))
                .thenReturn(true);

        assertThrows(
                PresenceDejaEnregistreeException.class,
                () -> presenceService.marquerPresence(
                        new MarkPresenceRequest("K7M4RX", 20L)
                )
        );

        verify(tentativePresenceRepository, never())
                .save(any(TentativePresence.class));
        verify(presenceRepository, never()).save(any(Presence.class));
    }

    @Test
    void doitBloquerALaCinquiemeTentativeIncorrecte() {

        Promotion promotion = promotion(1L);
        Etudiant etudiant = etudiant(20L, promotion);
        TentativePresence tentativePresence =
                new TentativePresence(etudiant, OffsetDateTime.now(clock));

        tentativePresence.setNombreTentativesIncorrectes(4);

        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(tentativePresenceRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(tentativePresence));
        when(courseSessionRepository.findByCode("INCONNU"))
                .thenReturn(Optional.empty());

        assertThrows(
                TentativesBloqueesException.class,
                () -> presenceService.marquerPresence(
                        new MarkPresenceRequest("INCONNU", 20L)
                )
        );

        assertEquals(5, tentativePresence.getNombreTentativesIncorrectes());
        assertEquals(
                OffsetDateTime.now(clock).plusMinutes(2),
                tentativePresence.getBloqueJusqua()
        );
        verify(presenceRepository, never()).save(any(Presence.class));
    }

    @Test
    void doitRefuserUneTentativePendantLeBlocage() {

        Promotion promotion = promotion(1L);
        Etudiant etudiant = etudiant(20L, promotion);
        TentativePresence tentativePresence =
                new TentativePresence(etudiant, OffsetDateTime.now(clock));

        tentativePresence.setNombreTentativesIncorrectes(5);
        tentativePresence.setBloqueJusqua(
                OffsetDateTime.now(clock).plusMinutes(1)
        );

        when(etudiantRepository.findById(20L))
                .thenReturn(Optional.of(etudiant));
        when(tentativePresenceRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(tentativePresence));

        assertThrows(
                TentativesBloqueesException.class,
                () -> presenceService.marquerPresence(
                        new MarkPresenceRequest("K7M4RX", 20L)
                )
        );

        verify(courseSessionRepository, never()).findByCode("K7M4RX");
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
        session.setOuvertureAt(OffsetDateTime.now(clock).minusMinutes(5));
        session.setExpirationAt(
                OffsetDateTime.now(clock).plusMinutes(minutesAvantExpiration)
        );
        session.setPromotion(promotion);

        return session;
    }
}
