# Journal de bord — 181

> Une entrée par étape, rédigée au fur et à mesure de l'avancement.
> Chaque entrée indique ce qui a été réalisé, les difficultés rencontrées
> et la manière dont les réponses de l'IA ont été vérifiées.

---

## Étape 1 — Analyse et conception

**Statut :** Terminé

### Fait

- Lecture et analyse du sujet, de `CLIENT.md`, du contrat API imposé et des modèles fournis.
- Vérification de l'environnement de travail :
    - Git 2.49.0 ;
    - Java 25 ;
    - Node.js 22.
- Création et vérification du dépôt GitHub public.
- Mise en place de la structure documentaire :
    - `docs/CAHIER_DES_CHARGES.md` ;
    - `docs/JOURNAL.md` ;
    - `docs/diagrammes/` ;
    - `api/contrat.yaml`.
- Rédaction du cahier des charges avec :
    - les acteurs et leurs rôles ;
    - le périmètre fonctionnel ;
    - les exigences fonctionnelles ;
    - les exigences non fonctionnelles ;
    - les règles de gestion ;
    - les hypothèses ;
    - les zones d'ombre ;
    - la contradiction Q10 / Q15 et la décision retenue.
- Décision prise pour Q10 / Q15 : permettre au relecteur de modifier sa relecture tant que le formateur n'a pas clôturé la session.
- Identification d'une zone non précisée concernant l'absence de relecteur admissible lorsque l'auteur est le seul étudiant présent.
- Réalisation des diagrammes en Mermaid :
    - D1 — cas d'utilisation ;
    - D2 — modèle de données ;
    - D3 — séquence « marquer sa présence » ;
    - D4 — diagramme bonus états-transitions du cycle de vie d'un exercice.
- Constitution du backlog sous forme d'issues GitHub identifiées par des codes `US-01`, `US-02`, etc.
- Ajout de critères d'acceptation, priorités et références `EFx` / `RGx` dans les issues.
- Mise en place d'un Project Board avec le workflow :
    - Backlog ;
    - Todo ;
    - In Progress ;
    - Test ;
    - Done.
- Prise en compte de la version révisée du sujet :
    - utilisation du terme `issue` à la place de `ticket` ;
    - passage de six à cinq étapes ;
    - suppression de l'épreuve Git séparée ;
    - nouvelle procédure d'obtention de l'enveloppe auprès du surveillant après `[JALON] v0.1`.
- Complétion et validation de `api/contrat.yaml` avec les cinq opérations imposées et les opérations supplémentaires nécessaires aux exigences retenues.
- Ajout du `.gitignore` avant le premier commit de code.
- Vérification finale de la cohérence entre le cahier des charges, les diagrammes, les issues et le contrat API.

### Bloqué

La principale difficulté a été la contradiction entre Q10 et Q15 :

- Q10 autorise le relecteur à corriger sa note tant que la session n'est pas clôturée ;
- Q15 affirme qu'une note validée devient définitive.

J'ai choisi de retenir Q10 et de considérer la clôture de la session comme le moment où la relecture devient définitivement non modifiable. Cette décision est documentée dans le cahier des charges.

Une deuxième difficulté concernait le diagramme de cas d'utilisation, notamment la distinction entre une action réellement effectuée par un acteur et un traitement automatique du système. Le diagramme a été corrigé pour représenter plus clairement le rôle temporaire de relecteur et l'affectation automatique.

La publication d'une version révisée du sujet a également nécessité une mise à jour de la documentation déjà commencée.

**Temps consacré aux blocages :** à compléter avec mon temps réel avant le jalon d'analyse.

### IA

J'ai utilisé l'IA pour :

- analyser le sujet et `CLIENT.md` ;
- identifier les exigences fonctionnelles et les règles de gestion ;
- repérer et analyser la contradiction Q10 / Q15 ;
- proposer une structure de cahier des charges ;
- produire une première version des diagrammes Mermaid ;
- contrôler la cohérence entre les diagrammes, le cahier des charges et le contrat API ;
- proposer le découpage du backlog en issues ;
- reformuler les critères d'acceptation des issues ;
- analyser l'impact de la version révisée du sujet.

Je n'ai pas repris les propositions automatiquement.

J'ai vérifié les réponses en les comparant avec :

- les questions Q1 à Q16 de `CLIENT.md` ;
- les cinq opérations imposées dans `api/contrat.yaml` ;
- les contraintes B1 à B6 et F1 à F3 ;
- les critères du barème ;
- la cohérence entre le cahier des charges, les issues et les diagrammes.

Certaines propositions ont été corrigées lorsqu'elles ajoutaient des comportements qui n'étaient pas explicitement demandés ou lorsqu'elles ne correspondaient pas exactement au contrat API.


### Clôture de l'étape

L'analyse, les diagrammes, le backlog et le contrat API ont été finalisés et vérifiés.

Le jalon `[JALON] analyse` peut être posé avant tout premier commit de code.

`[JALON] analyse`

---

## Étape 2 — Première version

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 3 — Enveloppe et conduite du changement

**Fait :**

**Bloqué :**

**IA :**

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :**

---

## Étape 4 — Version finale

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 5 — Soumission

**Fait :**

**Bloqué :**

**IA :**

**Ce que je referais autrement avec une journée de plus :**