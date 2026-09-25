CREATE TABLE exercices (
                           id BIGSERIAL PRIMARY KEY,
                           lien VARCHAR(2048) NOT NULL,
                           statut VARCHAR(40) NOT NULL,
                           depose_at TIMESTAMPTZ NOT NULL,
                           modifie_at TIMESTAMPTZ,
                           session_id BIGINT NOT NULL,
                           etudiant_id BIGINT NOT NULL,

                           CONSTRAINT fk_exercice_session
                               FOREIGN KEY (session_id)
                                   REFERENCES sessions(id),

                           CONSTRAINT fk_exercice_etudiant
                               FOREIGN KEY (etudiant_id)
                                   REFERENCES etudiants(id),

                           CONSTRAINT uk_exercice_session_etudiant
                               UNIQUE (session_id, etudiant_id),

                           CONSTRAINT chk_exercice_statut
                               CHECK (statut IN ('DEPOSE', 'EN_ATTENTE_RELECTURE', 'EN_COURS', 'RELU'))
);
