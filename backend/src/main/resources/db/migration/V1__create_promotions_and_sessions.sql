CREATE TABLE promotions (
                            id BIGSERIAL PRIMARY KEY,
                            nom VARCHAR(150) NOT NULL
);

CREATE TABLE sessions (
                          id BIGSERIAL PRIMARY KEY,
                          titre VARCHAR(255) NOT NULL,
                          code VARCHAR(20) NOT NULL UNIQUE,

                          ouverture_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          expiration_at TIMESTAMP WITH TIME ZONE NOT NULL,

                          fin_at TIMESTAMP WITH TIME ZONE,
                          cloture_at TIMESTAMP WITH TIME ZONE,

                          statut VARCHAR(20) NOT NULL DEFAULT 'OUVERTE',

                          promotion_id BIGINT NOT NULL,

                          CONSTRAINT fk_session_promotion
                              FOREIGN KEY (promotion_id)
                                  REFERENCES promotions(id),

                          CONSTRAINT chk_session_statut
                              CHECK (statut IN ('OUVERTE', 'TERMINEE', 'CLOTUREE')),

                          CONSTRAINT chk_session_expiration
                              CHECK (expiration_at > ouverture_at)
);