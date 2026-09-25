CREATE TABLE relectures (
                            id BIGSERIAL PRIMARY KEY,
                            statut VARCHAR(40) NOT NULL,
                            affectee_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            commencee_at TIMESTAMP WITH TIME ZONE,
                            rendue_at TIMESTAMP WITH TIME ZONE,
                            modifiee_at TIMESTAMP WITH TIME ZONE,
                            note INTEGER,
                            commentaire TEXT,
                            exercice_id BIGINT NOT NULL,
                            relecteur_id BIGINT NOT NULL,

                            CONSTRAINT fk_relecture_exercice
                                FOREIGN KEY (exercice_id)
                                    REFERENCES exercices(id),

                            CONSTRAINT fk_relecture_relecteur
                                FOREIGN KEY (relecteur_id)
                                    REFERENCES etudiants(id),

                            CONSTRAINT uk_relecture_exercice
                                UNIQUE (exercice_id),

                            CONSTRAINT chk_relecture_statut
                                CHECK (statut IN ('A_FAIRE', 'EN_COURS', 'RELU')),

                            CONSTRAINT chk_relecture_note
                                CHECK (note IS NULL OR (note >= 0 AND note <= 20))
);
