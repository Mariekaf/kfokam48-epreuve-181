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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "relectures")
public class Relecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RelectureStatus statut;

    @Column(name = "affectee_at", nullable = false)
    private OffsetDateTime affecteeAt;

    @Column(name = "commencee_at")
    private OffsetDateTime commenceeAt;

    @Column(name = "rendue_at")
    private OffsetDateTime rendueAt;

    @Column(name = "modifiee_at")
    private OffsetDateTime modifieeAt;

    @Column
    private Integer note;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercice_id", nullable = false, unique = true)
    private Exercice exercice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relecteur_id", nullable = false)
    private Etudiant relecteur;

    public Relecture() {
    }

    public Long getId() {
        return id;
    }

    public RelectureStatus getStatut() {
        return statut;
    }

    public void setStatut(RelectureStatus statut) {
        this.statut = statut;
    }

    public OffsetDateTime getAffecteeAt() {
        return affecteeAt;
    }

    public void setAffecteeAt(OffsetDateTime affecteeAt) {
        this.affecteeAt = affecteeAt;
    }

    public OffsetDateTime getCommenceeAt() {
        return commenceeAt;
    }

    public void setCommenceeAt(OffsetDateTime commenceeAt) {
        this.commenceeAt = commenceeAt;
    }

    public OffsetDateTime getRendueAt() {
        return rendueAt;
    }

    public void setRendueAt(OffsetDateTime rendueAt) {
        this.rendueAt = rendueAt;
    }

    public OffsetDateTime getModifieeAt() {
        return modifieeAt;
    }

    public void setModifieeAt(OffsetDateTime modifieeAt) {
        this.modifieeAt = modifieeAt;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Exercice getExercice() {
        return exercice;
    }

    public void setExercice(Exercice exercice) {
        this.exercice = exercice;
    }

    public Etudiant getRelecteur() {
        return relecteur;
    }

    public void setRelecteur(Etudiant relecteur) {
        this.relecteur = relecteur;
    }
}
