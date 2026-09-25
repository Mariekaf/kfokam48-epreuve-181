package com.kfokam48.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "exercices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exercice_session_etudiant",
                        columnNames = {"session_id", "etudiant_id"}
                )
        }
)
public class Exercice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String lien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ExerciceStatus statut;

    @Column(name = "depose_at", nullable = false)
    private OffsetDateTime deposeAt;

    @Column(name = "modifie_at")
    private OffsetDateTime modifieAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private CourseSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    public Exercice() {
    }

    public Long getId() {
        return id;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public ExerciceStatus getStatut() {
        return statut;
    }

    public void setStatut(ExerciceStatus statut) {
        this.statut = statut;
    }

    public OffsetDateTime getDeposeAt() {
        return deposeAt;
    }

    public void setDeposeAt(OffsetDateTime deposeAt) {
        this.deposeAt = deposeAt;
    }

    public OffsetDateTime getModifieAt() {
        return modifieAt;
    }

    public void setModifieAt(OffsetDateTime modifieAt) {
        this.modifieAt = modifieAt;
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
