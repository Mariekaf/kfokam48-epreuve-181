CREATE TABLE tentatives_presence (
                                      id BIGSERIAL PRIMARY KEY,
                                      etudiant_id BIGINT NOT NULL,
                                      nombre_tentatives_incorrectes INTEGER NOT NULL DEFAULT 0,
                                      bloque_jusqua TIMESTAMP WITH TIME ZONE,
                                      updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                      CONSTRAINT fk_tentative_presence_etudiant
                                          FOREIGN KEY (etudiant_id)
                                              REFERENCES etudiants(id),

                                      CONSTRAINT uk_tentative_presence_etudiant
                                          UNIQUE (etudiant_id),

                                      CONSTRAINT chk_tentative_presence_nombre
                                          CHECK (nombre_tentatives_incorrectes >= 0)
);
