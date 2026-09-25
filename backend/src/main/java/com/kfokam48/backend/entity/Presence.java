package com.kfokam48.backend.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "presences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_presence_session_etudiant",
                        columnNames = {"session_id", "etudiant_id"}
                )
        }
)
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PresenceSource source;

    @Column(name = "enregistree_at", nullable = false)
    private OffsetDateTime enregistreeAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private CourseSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    public Presence() {
    }

    public Long getId() {
        return id;
    }

    public PresenceSource getSource() {
        return source;
    }

    public void setSource(PresenceSource source) {
        this.source = source;
    }

    public OffsetDateTime getEnregistreeAt() {
        return enregistreeAt;
    }

    public void setEnregistreeAt(OffsetDateTime enregistreeAt) {
        this.enregistreeAt = enregistreeAt;
    }

    public CourseSession getSession() {
        return session;
    }

    public void setSession(CourseSession session) {
        this.session = session;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }
}