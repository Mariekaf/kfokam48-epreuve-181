package com.kfokam48.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tentatives_presence")
public class TentativePresence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false, unique = true)
    private Etudiant etudiant;

    @Column(name = "nombre_tentatives_incorrectes", nullable = false)
    private int nombreTentativesIncorrectes;

    @Column(name = "bloque_jusqua")
    private OffsetDateTime bloqueJusqua;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public TentativePresence() {
    }

    public TentativePresence(Etudiant etudiant, OffsetDateTime updatedAt) {
        this.etudiant = etudiant;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public int getNombreTentativesIncorrectes() {
        return nombreTentativesIncorrectes;
    }

    public void setNombreTentativesIncorrectes(int nombreTentativesIncorrectes) {
        this.nombreTentativesIncorrectes = nombreTentativesIncorrectes;
    }

    public OffsetDateTime getBloqueJusqua() {
        return bloqueJusqua;
    }

    public void setBloqueJusqua(OffsetDateTime bloqueJusqua) {
        this.bloqueJusqua = bloqueJusqua;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
