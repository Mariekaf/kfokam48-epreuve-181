# Cahier des charges — Plateforme de suivi des sessions KFOKAM48

**Auteur :** 181  
**Version :** 1  
**Frontend choisi :** Angular, parce qu'il permet de structurer clairement l'application en composants et services, notamment pour isoler les appels à l'API dans une couche dédiée.

---

## 1. Contexte et objectif

La direction de la formation KFOKAM48 souhaite disposer d'une application permettant de centraliser le suivi des sessions de cours, des présences, des exercices déposés par les étudiants et des relectures effectuées entre pairs.

Lors d'une session de formation, le formateur doit pouvoir ouvrir une session et obtenir un code de présence. Les étudiants présents utilisent ce code afin d'enregistrer leur présence.

Les étudiants peuvent ensuite déposer le lien de leur exercice pour la session. Un étudiant présent peut être affecté automatiquement par le système à la relecture de l'exercice d'un autre étudiant. Cette relecture comprend une note et un commentaire.

Le formateur doit enfin disposer d'un tableau synthétique lui permettant de suivre, pour chaque étudiant :

- ses présences ;
- le nombre d'exercices déposés ;
- la moyenne des notes reçues ;
- les relectures restant à effectuer.

L'objectif de l'application est donc de fournir un outil simple permettant de gérer le cycle d'une session de formation, depuis son ouverture jusqu'au suivi des présences, des exercices et des relectures.

---

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire |
|---|---|
| Formateur | Ouvrir une session, obtenir un code de présence, ajouter manuellement une présence, consulter le tableau de suivi et clôturer une session. |
| Étudiant | Choisir son identité dans une liste, marquer sa présence avec le code de session, déposer son exercice, éventuellement remplacer le lien de son exercice et consulter la note et le commentaire reçus. |
| Étudiant agissant comme relecteur | Consulter la relecture qui lui a été affectée, attribuer une note entière de 0 à 20, saisir un commentaire et soumettre sa relecture. |

### Précision sur le rôle de relecteur

Le relecteur n'est pas une catégorie distincte d'utilisateur.

Il s'agit d'un étudiant auquel le système affecte temporairement la relecture de l'exercice d'un autre étudiant de la même session.

Un étudiant ne peut jamais être affecté à la relecture de son propre exercice.

---

## 3. Périmètre

### Inclus

Le périmètre de cette version comprend :

- la gestion des sessions de cours ;
- la génération d'un code de présence ;
- l'expiration du code de présence ;
- l'enregistrement d'une présence par un étudiant ;
- la prévention des présences en double ;
- le blocage temporaire après plusieurs codes incorrects ;
- l'ajout manuel d'une présence par le formateur ;
- l'identification de l'origine d'une présence ;
- le dépôt d'un lien d'exercice ;
- le remplacement du lien d'un exercice sous conditions ;
- l'affectation automatique d'un relecteur ;
- la saisie d'une note et d'un commentaire ;
- le suivi de l'état d'une relecture ;
- la consultation du résultat d'une relecture par l'étudiant concerné ;
- la consultation du tableau de suivi par le formateur ;
- le calcul de la moyenne des notes côté backend ;
- la clôture d'une session ;
- la gestion des erreurs fonctionnelles définies dans le contrat API.

### Exclus

Ne font pas partie du périmètre de cette version :

- l'inscription autonome d'un étudiant ;
- la création d'un système complet de comptes utilisateurs ;
- la gestion des mots de passe ;
- la récupération de mot de passe ;
- l'envoi d'e-mails ou de SMS ;
- les notifications push ;
- le dépôt physique d'un fichier d'exercice ;
- la messagerie instantanée entre étudiants ;
- plusieurs relecteurs pour un même exercice ;
- la notation avec des nombres décimaux ;
- la modification manuelle d'une note par le formateur ;
- un module complet d'administration des promotions et des étudiants ;
- les fonctionnalités non nécessaires au besoin décrit dans le sujet.

Les étudiants et promotions nécessaires à la démonstration pourront être fournis sous forme de données de démonstration.

---

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | Permettre au formateur d'ouvrir une session de cours | Lorsqu'un formateur ouvre une session avec un titre et une promotion valides, le système crée la session et retourne son identifiant, un code de présence, la date d'ouverture et la date d'expiration du code. | Must |
| EF2 | Permettre à un étudiant de marquer sa présence avec le code d'une session | Lorsqu'un étudiant saisit un code valide et non expiré, sa présence est enregistrée une seule fois pour la session et la source vaut `ETUDIANT`. | Must |
| EF3 | Gérer les erreurs de saisie du code de présence | Un code inconnu est refusé. Un code expiré est refusé. Une seconde présence du même étudiant à la même session est refusée. Après cinq codes incorrects, l'étudiant est temporairement bloqué pendant deux minutes. | Must |
| EF4 | Permettre au formateur d'ajouter une présence manuellement | Lorsqu'un formateur ajoute manuellement la présence d'un étudiant, celle-ci apparaît dans le suivi avec la source `FORMATEUR`. | Should |
| EF5 | Permettre à un étudiant de déposer son exercice | Pour une session donnée, un étudiant peut enregistrer un lien valide vers son exercice. Une seconde création pour le même étudiant et la même session est refusée. | Must |
| EF6 | Permettre à l'étudiant de remplacer le lien de son exercice | Tant que la relecture de l'exercice n'a pas commencé, l'étudiant peut remplacer son lien. Dès que la relecture est considérée comme commencée, le remplacement est refusé. | Should |
| EF7 | Affecter automatiquement un relecteur à un exercice | Le système sélectionne un étudiant présent à la session, différent de l'auteur de l'exercice. Un exercice ne possède qu'un seul relecteur. | Must |
| EF8 | Permettre au relecteur d'effectuer une relecture | Le relecteur affecté peut saisir une note entière comprise entre 0 et 20 ainsi qu'un commentaire. Une note invalide est refusée. | Must |
| EF9 | Permettre la modification d'une relecture avant la clôture de la session | Tant que la session n'est pas clôturée, le relecteur peut corriger la note et/ou le commentaire de sa relecture conformément à la décision prise pour la contradiction Q10/Q15. Après clôture, la modification est refusée. | Should |
| EF10 | Permettre à l'étudiant de consulter le résultat reçu | Lorsqu'une relecture a été rendue, l'auteur de l'exercice peut consulter sa note et le commentaire, mais le nom du relecteur ne lui est pas affiché. | Should |
| EF11 | Permettre au formateur de consulter le tableau de suivi | Pour une promotion valide, le tableau présente pour chaque étudiant son nom, ses présences, son nombre d'exercices déposés, sa moyenne et ses relectures en attente. | Must |
| EF12 | Permettre au formateur de clôturer une session | Lorsqu'une session est clôturée, les opérations explicitement interdites après clôture, notamment le dépôt tardif ou la modification d'une relecture, ne sont plus acceptées. | Must |

---

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | Le backend doit utiliser Java 17 ou une version supérieure avec Spring Boot et Maven. | Vérifier `java -version`, `pom.xml` et l'exécution du projet. |
| ENF2 | Le wrapper Maven `mvnw` doit être présent dans le dépôt. | Vérifier la présence de `mvnw`, `mvnw.cmd` et du dossier `.mvn`. |
| ENF3 | L'API doit respecter le contrat défini dans `api/contrat.yaml`. | Comparer les chemins, verbes HTTP, corps des requêtes, réponses et codes HTTP avec le contrat. |
| ENF4 | L'architecture backend doit séparer les contrôleurs, services et repositories. | Vérification de la structure du projet et revue du code. |
| ENF5 | Les entités JPA ne doivent pas être exposées directement par l'API. | Vérifier que les contrôleurs utilisent des DTO pour les entrées et sorties. |
| ENF6 | Les entrées utilisateur doivent être validées côté backend. | Vérifier les annotations de validation et les tests sur les données invalides. |
| ENF7 | Les erreurs doivent être gérées de manière centralisée. | Vérifier la présence d'un `@RestControllerAdvice` et le format uniforme des erreurs. |
| ENF8 | Aucune stack trace ni page d'erreur Spring par défaut ne doit être renvoyée au frontend. | Tester plusieurs erreurs et contrôler leur corps HTTP. |
| ENF9 | Le schéma de base de données doit être versionné avec Flyway ou Liquibase. | Vérifier la présence et l'exécution des migrations. |
| ENF10 | `ddl-auto=update` ne doit pas être utilisé hors environnement de test. | Vérifier les fichiers de configuration Spring. |
| ENF11 | Le backend doit contenir au minimum un test unitaire portant sur une règle métier réelle et un test d'intégration portant sur un endpoint. | Exécuter la suite de tests sur un environnement vierge. |
| ENF12 | Le frontend doit utiliser Angular et son build doit réussir. | Exécuter la commande de build documentée. |
| ENF13 | Les appels HTTP du frontend doivent être centralisés dans une couche dédiée. | Vérifier l'utilisation de services Angular dédiés aux appels API. |
| ENF14 | Le frontend doit afficher explicitement les états de chargement et d'erreur. | Tester les écrans lorsque l'API répond lentement ou retourne une erreur. |
| ENF15 | Les règles métier et calculs métier ne doivent pas être dupliqués dans le frontend. | Vérifier notamment que la moyenne affichée provient directement de l'API. |
| ENF16 | L'application doit être utilisable sur un écran d'ordinateur et les principales actions étudiantes doivent rester utilisables sur un écran mobile. | Tester les principaux écrans sur plusieurs tailles de viewport. |
| ENF17 | Pour les données de démonstration de l'épreuve, une réponse API courante doit être obtenue en moins de deux secondes dans l'environnement local de test. | Mesurer les requêtes principales pendant les tests. |
| ENF18 | Le projet doit pouvoir être démarré depuis un clone vierge avec les instructions du README. | Cloner le dépôt dans un autre dossier et suivre uniquement les instructions documentées. |
| ENF19 | Le démarrage complet doit nécessiter au maximum trois commandes si Docker Compose n'est pas utilisé. | Compter les commandes documentées dans le README. |
| ENF20 | Des données de démonstration doivent être disponibles afin de pouvoir tester immédiatement l'application. | Démarrer l'application et vérifier qu'une promotion et plusieurs étudiants utilisables sont disponibles. |

### Hypothèse de volumétrie

L'application réalisée dans le cadre de l'épreuve n'est pas destinée à démontrer une architecture à très grande échelle.

Pour la conception et les tests, l'hypothèse retenue est qu'une promotion peut contenir jusqu'à environ 200 étudiants sans dégradation perceptible des fonctionnalités principales.

Cette valeur est une hypothèse de dimensionnement du projet et non une limite métier exprimée par le client.

---

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Aucun mot de passe n'est requis pour l'étudiant ; il sélectionne son identité dans une liste. | Q1 |
| RG2 | Le code de présence expire 15 minutes après l'ouverture de la session. | Q2 |
| RG3 | Un étudiant ne peut pas marquer sa présence après la fin de la période autorisée pour la session. | Q3 |
| RG4 | Après cinq saisies de codes incorrects, l'étudiant est bloqué pendant deux minutes avant de pouvoir réessayer. | Q4 |
| RG5 | Un étudiant ne peut jamais relire son propre exercice. | Q5 |
| RG6 | Un exercice ne peut avoir qu'un seul relecteur. | Q6 |
| RG7 | Le relecteur est choisi automatiquement par le système parmi les étudiants présents à la session. | Q7 |
| RG8 | L'étudiant relu peut consulter sa note et le commentaire reçu, mais ne doit pas connaître l'identité de son relecteur. | Q8 |
| RG9 | Une note doit être un nombre entier compris entre 0 et 20 inclus. | Q9 |
| RG10 | La décision retenue est qu'une relecture peut être corrigée par son relecteur tant que la session n'est pas clôturée. | Q10 / Q15, décision projet |
| RG11 | Une relecture qui n'a pas été rendue reste explicitement dans l'état `EN_ATTENTE`. | Q11 |
| RG12 | Un étudiant peut déposer son exercice après la fin du cours tant que le formateur n'a pas clôturé la session. | Q12 |
| RG13 | Un étudiant peut remplacer le lien de son exercice uniquement tant que personne n'a commencé sa relecture. | Q13 |
| RG14 | Le formateur peut ajouter manuellement une présence. Cette présence doit être identifiable comme ayant été ajoutée par le formateur. | Q14 |
| RG15 | Une présence possède une source égale à `ETUDIANT` ou `FORMATEUR`. | Contrat API |
| RG16 | Un étudiant ne peut être présent qu'une seule fois à une même session. | Contrat API |
| RG17 | Un étudiant ne peut déposer qu'un seul exercice par session ; une tentative de second dépôt est refusée. | Contrat API |
| RG18 | La moyenne présentée au formateur est calculée par le backend à partir des notes reçues et non par le frontend. | Contrainte F3 |
| RG19 | Une promotion inconnue demandée dans le tableau produit une erreur fonctionnelle. | Contrat API |
| RG20 | Toutes les erreurs de l'API utilisent un objet contenant au minimum les propriétés `code` et `message`. | Contrat API |

---

## 7. Zones d'ombre, hypothèses et contradictions

| Point | Réponse client (Qx) ou hypothèse | Décision retenue | Pourquoi |
|---|---|---|---|
| Modification d'une relecture | Q10 indique qu'un relecteur peut corriger sa note tant que la session n'est pas clôturée, alors que Q15 indique qu'une note validée est définitive. | La règle de Q10 est retenue : le relecteur peut modifier sa relecture jusqu'à la clôture de la session. Après clôture, elle devient définitive. | Les deux réponses sont contradictoires. Q10 fournit une limite temporelle précise et exploitable. La clôture de la session constitue donc le verrou définitif retenu par le projet. |
| Étudiant unique ou absence de relecteur admissible | Q5 interdit l'auto-relecture et Q7 impose un relecteur choisi parmi les étudiants présents. Le client ne précise pas le comportement lorsqu'aucun autre étudiant présent n'est disponible. | L'exercice reste `EN_ATTENTE_AFFECTATION` jusqu'à ce qu'un étudiant admissible puisse être désigné. Aucune auto-relecture n'est créée pour contourner le problème. | Cette décision garantit le respect absolu de RG5. |
| Moment de l'affectation du relecteur | Le client précise qui choisit le relecteur, mais pas précisément à quel événement l'affectation est déclenchée. | L'affectation est tentée après le dépôt d'un exercice. Si aucun relecteur admissible n'est disponible, elle peut être retentée ultérieurement. | Le dépôt constitue le premier moment où un exercice existe réellement et peut être relu. |
| Début d'une relecture | Q13 interdit de remplacer un lien lorsque quelqu'un a commencé à le relire, mais ne définit pas techniquement ce que signifie « commencé ». | Une relecture est considérée comme commencée lorsqu'elle passe de l'état `A_FAIRE` à `EN_COURS`. | Cette décision fournit un état vérifiable permettant d'appliquer RG13 sans ambiguïté. |
| Fin du cours et clôture | Q12 indique qu'un exercice peut encore être déposé après la fin de la session jusqu'à sa clôture. | La fin pédagogique du cours et la clôture applicative sont considérées comme deux notions différentes. La clôture effectuée par le formateur interdit les nouveaux dépôts et les modifications qui dépendent encore d'une session ouverte. | Q12 suppose explicitement qu'une période peut exister entre la fin du cours et la clôture. |
| Authentification | Q1 précise qu'aucun mot de passe n'est nécessaire et que l'étudiant sélectionne son nom dans une liste. | Aucun système d'authentification complexe n'est développé pour l'épreuve. L'identité de l'étudiant est sélectionnée parmi les données disponibles. | Le client demande explicitement de ne pas consacrer de temps à cette fonctionnalité. |
| Données de promotions et étudiants | Le contrat utilise `promotionId` et `etudiantId`, mais aucune fonctionnalité de gestion administrative de ces données n'est décrite. | Des promotions et étudiants de démonstration seront chargés au démarrage. Aucun CRUD complet d'administration n'est prévu. | Cela permet d'utiliser les fonctionnalités demandées sans ajouter un module non exprimé par le client. |
| Commentaire de relecture vide | Le client demande « note et commentaire » mais ne précise pas si le commentaire peut être vide. | Le commentaire est considéré comme obligatoire lors de la soumission d'une relecture. | Une relecture entre pairs sans commentaire ne répondrait que partiellement au besoin exprimé. |
| Moyenne lorsqu'aucune note n'existe | Le client demande une moyenne, mais ne précise pas le résultat pour un étudiant n'ayant reçu aucune note. | La moyenne est retournée à `null` lorsqu'aucune note n'est disponible. | `0` pourrait être interprété à tort comme une note réellement obtenue. |

---

## 8. Contraintes techniques

### Backend

Le backend doit respecter les contraintes suivantes :

- Java 17 ou supérieur ;
- Spring Boot ;
- Maven ;
- wrapper Maven `mvnw` versionné dans le dépôt ;
- séparation des couches :
    - Controller ;
    - Service ;
    - Repository ;
- utilisation de DTO pour les échanges avec l'API ;
- aucune entité JPA directement exposée dans les réponses JSON ;
- validation des entrées ;
- gestion centralisée des erreurs avec `@RestControllerAdvice` ;
- aucune stack trace retournée au client ;
- respect exact de `api/contrat.yaml` ;
- utilisation de Flyway ou Liquibase pour versionner le schéma de base de données ;
- interdiction de `ddl-auto=update` hors tests ;
- au minimum :
    - un test unitaire portant sur une règle métier réelle ;
    - un test d'intégration portant sur un endpoint.

### Frontend

Le frontend utilise Angular.

Il doit contenir au minimum les écrans nécessaires aux trois usages principaux :

#### Espace formateur

Le formateur peut :

- ouvrir une session ;
- consulter le code de présence ;
- consulter le tableau de suivi ;
- effectuer les actions prévues sur la session.

#### Espace étudiant

L'étudiant peut :

- sélectionner son identité ;
- saisir un code de présence ;
- déposer son exercice ;
- consulter les informations qui le concernent.

#### Espace relecteur

L'étudiant agissant comme relecteur peut :

- consulter la relecture qui lui est affectée ;
- saisir une note ;
- saisir un commentaire ;
- soumettre ou modifier sa relecture conformément aux règles de gestion.

Les appels à l'API doivent être regroupés dans des services dédiés.

Le frontend doit gérer :

- les états de chargement ;
- les réponses réussies ;
- les erreurs fonctionnelles de l'API ;
- les erreurs techniques.

Les règles métier ne doivent pas être recalculées ou dupliquées côté frontend.

En particulier, la moyenne affichée dans le tableau doit être celle fournie par le backend.

### Contrat API

Les cinq opérations imposées par le sujet doivent exister exactement comme définies dans `api/contrat.yaml` :

- `POST /api/sessions`
- `POST /api/presences`
- `POST /api/exercices`
- `POST /api/relectures/{id}`
- `GET /api/tableau?promotionId=...`

D'autres opérations pourront être ajoutées lorsque leur nécessité découle des exigences fonctionnelles, sans modifier le comportement imposé des cinq opérations obligatoires.

### Format des erreurs

Toutes les erreurs exposées par l'API doivent respecter une structure uniforme de la forme :

```json
{
  "code": "CODE_ERREUR",
  "message": "Description compréhensible de l'erreur."
}
```

### Démarrage

Le projet devra pouvoir être démarré :

- soit avec `docker compose up` ;
- soit avec un maximum de trois commandes documentées dans le README.

Les instructions devront être testées depuis un clone vierge.

Des données de démonstration devront être chargées afin de permettre au correcteur de tester immédiatement l'application.

---

## 9. Livrables

Les livrables du projet sont :

### Documentation

- `docs/CAHIER_DES_CHARGES.md`
- `docs/JOURNAL.md`
- `docs/diagrammes/`
    - diagramme de cas d'utilisation ;
    - diagramme de classes ou modèle de données ;
    - diagramme de séquence « marquer sa présence » ;
    - diagramme états-transitions du cycle de vie d'un exercice, si réalisé ;
- `README.md`
- `CHANGELOG.md`
- `SOUMISSION.md`

### API

- `api/contrat.yaml` complété et maintenu à jour.

### Backend

- application Spring Boot ;
- migrations de base de données ;
- données de démonstration ;
- tests unitaires ;
- tests d'intégration.

### Frontend

- application Angular ;
- écran formateur ;
- écran étudiant ;
- écran relecteur ;
- couche dédiée aux appels API ;
- gestion des états de chargement et d'erreur.

### Gestion de projet

- backlog sous forme d'issues GitHub ;
- branches associées aux tickets ;
- pull requests associées aux branches ;
- historique Git propre et progressif ;
- jalons :
    - `[JALON] analyse`
    - `[JALON] v0.1`
    - `[JALON] v1.0`

### Épreuve Git

Un second dépôt public distinct sera créé pour l'étape consacrée à l'épreuve Git à partir du bundle fourni.

---

## 10. Démarche prévue

Le développement sera réalisé conformément aux cinq étapes définies par le sujet.

### Étape 1 — Analyse et conception

Avant toute implémentation :

1. analyser le besoin du client ;
2. rédiger le cahier des charges ;
3. identifier les exigences fonctionnelles et non fonctionnelles ;
4. formaliser les règles de gestion ;
5. documenter les zones d'ombre, hypothèses et contradictions ;
6. produire les diagrammes ;
7. constituer le backlog sous forme d'issues GitHub ;
8. compléter et figer le contrat API ;
9. mettre à jour le journal ;
10. poser le jalon :

```text
[JALON] analyse
```

Aucun code applicatif ne doit précéder ce jalon.

### Étape 2 — Première version v0.1

Les issues de priorité `Must` sont développées en premier.

Pour chaque issue :

1. sélectionner l'issue ;
2. créer une branche dédiée ;
3. implémenter la fonctionnalité ;
4. exécuter les tests concernés ;
5. effectuer des commits atomiques et explicites ;
6. pousser régulièrement la branche ;
7. ouvrir une pull request liée à l'issue ;
8. intégrer la pull request uniquement si `main` reste fonctionnel ;
9. fermer l'issue lorsque ses critères d'acceptation sont satisfaits.

Lorsque la première version est terminée, poser :

```text
[JALON] v0.1
```

### Étape 3 — Gestion du changement

Une fois `[JALON] v0.1` poussé, demander l'enveloppe au surveillant.

Avant de modifier le code :

1. lire le bug signalé et le nouveau besoin ;
2. reproduire le bug ;
3. créer les issues correspondantes avant de coder ;
4. analyser les impacts sur l'application ;
5. mettre à jour le cahier des charges ;
6. mettre à jour les diagrammes concernés ;
7. mettre à jour le contrat API ;
8. ajouter les migrations nécessaires ;
9. reprioriser le backlog ;
10. traiter séparément le correctif du bug et l'évolution fonctionnelle.

### Étape 4 — Version finale

Finaliser les fonctionnalités retenues et vérifier :

- la conformité fonctionnelle ;
- la conformité au contrat API ;
- les migrations ;
- les tests ;
- le frontend ;
- les données de démonstration ;
- le démarrage à partir d'un clone vierge.

Poser ensuite :

```text
[JALON] v1.0
```

Puis finaliser :

- `CHANGELOG.md` ;
- `README.md` ;
- le backlog restant ;
- `JOURNAL.md`.

### Étape 5 — Soumission

À la fin de l'épreuve :

1. vérifier que le dépôt GitHub est public ;
2. vérifier que tout le travail est poussé ;
3. relever le hash complet du commit final ;
4. tester le dépôt depuis une fenêtre de navigation privée ;
5. vérifier que le README permet de démarrer le projet depuis un clone vierge ;
6. compléter `SOUMISSION.md` ;
7. téléverser la soumission sur la plateforme avant 18h00.

---

## Definition of Done

Une issue est considérée comme terminée lorsque :

- ses critères d'acceptation sont satisfaits ;
- les exigences fonctionnelles et règles de gestion concernées sont respectées ;
- le travail a été réalisé sur une branche dédiée ;
- les modifications sont enregistrées dans des commits atomiques et explicites ;
- aucun secret ni fichier généré interdit n'est versionné ;
- les tests concernés passent ;
- le contrat API est respecté lorsqu'un endpoint est concerné ;
- les migrations sont ajoutées lorsqu'une modification du schéma est nécessaire ;
- la documentation impactée est mise à jour ;
- une pull request liée à l'issue a été créée ;
- la branche peut être intégrée sans rendre `main` non fonctionnel ;
- l'issue peut être fermée après intégration.

## Definition of Done

Une issue est considérée comme terminée lorsque :

- les critères d'acceptation associés sont satisfaits ;
- les règles de gestion concernées sont respectées ;
- le code est placé sur une branche dédiée ;
- les modifications sont enregistrées dans des commits atomiques et explicites ;
- aucun secret ou fichier généré interdit n'est versionné ;
- les tests concernés passent ;
- le contrat API est respecté lorsqu'un endpoint est concerné ;
- les migrations sont ajoutées lorsqu'une modification du schéma est nécessaire ;
- la documentation impactée est mise à jour ;
- une pull request liée à l'issue a été créée ;
- la branche peut être intégrée sans rendre `main` non fonctionnel ;
- l'issue peut être fermée après intégration.