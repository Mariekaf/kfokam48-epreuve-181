CREATE TABLE etudiants (
                           id BIGSERIAL PRIMARY KEY,
                           nom VARCHAR(150) NOT NULL,
                           promotion_id BIGINT NOT NULL,

                           CONSTRAINT fk_etudiant_promotion
                               FOREIGN KEY (promotion_id)
                                   REFERENCES promotions(id)
);

CREATE TABLE presences (
                           id BIGSERIAL PRIMARY KEY,
                           source VARCHAR(20) NOT NULL,
                           enregistree_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           session_id BIGINT NOT NULL,
                           etudiant_id BIGINT NOT NULL,

                           CONSTRAINT fk_presence_session
                               FOREIGN KEY (session_id)
                                   REFERENCES sessions(id),

                           CONSTRAINT fk_presence_etudiant
                               FOREIGN KEY (etudiant_id)
                                   REFERENCES etudiants(id),

                           CONSTRAINT uk_presence_session_etudiant
                               UNIQUE (session_id, etudiant_id),

                           CONSTRAINT chk_presence_source
                               CHECK (source IN ('ETUDIANT', 'FORMATEUR'))
);