# Changelog

## Version finale — 2026-09-25

- Finalisation de l'interface Angular Examinateur/Formateur, Étudiant et Relecteur.
- Ajout de la gestion de fin et de clôture des sessions.
- Ajout des présences manuelles avec source `FORMATEUR`.
- Ajout du remplacement du lien d'exercice avant démarrage de la relecture.
- Ajout de la consultation, du démarrage et de la correction des relectures.
- Ajout de la consultation des résultats étudiant sans exposition de l'identité du relecteur.
- Correction de l'affectation pour garantir un seul relecteur par exercice et interdire l'auto-relecture.
- Ajout des données de démonstration Flyway et d'une base H2 locale par défaut.
- Ajout des endpoints de référence nécessaires au frontend et mise à jour du contrat OpenAPI.
- Ajout d'un README de démarrage reproductible en trois commandes maximum.
- Uniformisation des erreurs fonctionnelles sous la forme `{code, message}`.
