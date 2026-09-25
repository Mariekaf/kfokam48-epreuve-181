INSERT INTO promotions (nom) VALUES ('KFOKAM48 - Démo');

INSERT INTO etudiants (nom, promotion_id)
SELECT 'Amina N.', id FROM promotions WHERE nom = 'KFOKAM48 - Démo';
INSERT INTO etudiants (nom, promotion_id)
SELECT 'Boris T.', id FROM promotions WHERE nom = 'KFOKAM48 - Démo';
INSERT INTO etudiants (nom, promotion_id)
SELECT 'Clarisse M.', id FROM promotions WHERE nom = 'KFOKAM48 - Démo';
INSERT INTO etudiants (nom, promotion_id)
SELECT 'David K.', id FROM promotions WHERE nom = 'KFOKAM48 - Démo';
INSERT INTO etudiants (nom, promotion_id)
SELECT 'Estelle F.', id FROM promotions WHERE nom = 'KFOKAM48 - Démo';
