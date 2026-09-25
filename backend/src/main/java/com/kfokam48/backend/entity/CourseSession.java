package com.kfokam48.backend.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "sessions")
public class CourseSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titre;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "ouverture_at", nullable = false)
    private OffsetDateTime ouvertureAt;

    @Column(name = "expiration_at", nullable = false)
    private OffsetDateTime expirationAt;

    @Column(name = "fin_at")
    private OffsetDateTime finAt;

    @Column(name = "cloture_at")
    private OffsetDateTime clotureAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus statut = SessionStatus.OUVERTE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    public CourseSession() {
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OffsetDateTime getOuvertureAt() {
        return ouvertureAt;
    }

    public void setOuvertureAt(OffsetDateTime ouvertureAt) {
        this.ouvertureAt = ouvertureAt;
    }

    public OffsetDateTime getExpirationAt() {
        return expirationAt;
    }

    public void setExpirationAt(OffsetDateTime expirationAt) {
        this.expirationAt = expirationAt;
    }

    public OffsetDateTime getFinAt() {
        return finAt;
    }

    public void setFinAt(OffsetDateTime finAt) {
        this.finAt = finAt;
    }

    public OffsetDateTime getClotureAt() {
        return clotureAt;
    }

    public void setClotureAt(OffsetDateTime clotureAt) {
        this.clotureAt = clotureAt;
    }

    public SessionStatus getStatut() {
        return statut;
    }

    public void setStatut(SessionStatus statut) {
        this.statut = statut;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
}