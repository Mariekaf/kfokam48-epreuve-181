# KFOKAM48 — Plateforme de suivi des sessions (181)

Application fullstack **Spring Boot + Angular** couvrant le cycle d'une session : présence, dépôt d'exercice, relecture par un pair et tableau de suivi formateur.

## Prérequis

- Java 17 ou supérieur
- Node.js 20 ou supérieur / npm

Aucun serveur de base de données n'est requis pour la démonstration : une base H2 locale est créée automatiquement et Flyway charge une promotion ainsi que cinq étudiants de démonstration.

## Démarrage depuis un clone vierge

Ouvrir deux terminaux.

**Terminal backend — commande 1 :**

```powershell
cd backend; .\mvnw.cmd spring-boot:run
```

Sous Linux/macOS : `cd backend && ./mvnw spring-boot:run`.

**Terminal frontend — commandes 2 et 3 :**

```powershell
cd frontend; npm ci
npm start
```

Ouvrir ensuite **http://localhost:4200**. Le serveur Angular redirige `/api` vers `http://localhost:8080` grâce à `proxy.conf.json`.

## Parcours de démonstration

Dans **Examinateur / Formateur**, sélectionner la promotion de démonstration, ouvrir une session et récupérer le code généré. Il est ensuite possible d'ajouter une présence manuelle, de terminer/clôturer la session et de consulter le tableau synthétique.

Dans **Espace étudiant**, choisir une identité, saisir le code de présence, déposer un lien d'exercice, consulter les résultats reçus et effectuer les relectures affectées.

## Tests et build

```powershell
cd backend; .\mvnw.cmd test
cd ..\frontend; npm run build
```

## Base PostgreSQL optionnelle

Le backend peut également utiliser PostgreSQL sans modification de code : définir `DB_URL`, `DB_USERNAME` et `DB_PASSWORD` avant le démarrage. Le schéma reste intégralement géré par Flyway.

## Contrat et documentation

- Contrat OpenAPI : `api/contrat.yaml`
- Cahier des charges : `docs/CAHIER_DES_CHARGES.md`
- Journal de bord : `docs/JOURNAL.md`
- Soumission : `docs/SOUMISSION.md`
