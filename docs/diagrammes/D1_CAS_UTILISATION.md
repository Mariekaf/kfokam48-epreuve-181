# D1 — Diagramme de cas d'utilisation

Ce diagramme présente les acteurs de l'application KFOKAM48 et les principales actions qu'ils peuvent réaliser.

```mermaid
flowchart LR

    F[👤 Formateur]
    E[👤 Étudiant]
    R[👤 Étudiant agissant comme relecteur]

    subgraph APP["Application de suivi KFOKAM48"]

        UC1([Ouvrir une session et obtenir un code de présence])
        UC2([Ajouter manuellement une présence])
        UC3([Consulter le tableau de suivi])
        UC4([Clôturer une session])

        UC5([Sélectionner son identité])
        UC6([Marquer sa présence])
        UC7([Déposer un exercice])
        UC8([Remplacer le lien de son exercice])
        UC9([Consulter sa note et son commentaire])

        UC10([Consulter une relecture affectée])
        UC11([Soumettre une note et un commentaire])
        UC12([Modifier une relecture avant clôture])

        UC13([Affecter automatiquement un relecteur])
    end

    F --> UC1
    F --> UC2
    F --> UC3
    F --> UC4

    E --> UC5
    E --> UC6
    E --> UC7
    E --> UC8
    E --> UC9

    R --> UC10
    R --> UC11
    R --> UC12

    R -. "est un rôle de" .-> E

    UC7 -. "déclenche une tentative d'affectation" .-> UC13
    UC13 -. "rend disponible" .-> UC10
```

## Contraintes principales

- Le code de présence expire 15 minutes après l'ouverture de la session.
- Un étudiant ne peut être présent qu'une seule fois à une même session.
- Après cinq codes incorrects, l'étudiant est bloqué pendant deux minutes.
- Un étudiant ne peut jamais relire son propre exercice.
- Un exercice ne possède qu'un seul relecteur.
- Le relecteur est choisi automatiquement parmi les étudiants présents.
- Une note doit être un entier compris entre 0 et 20.
- Le nom du relecteur n'est pas communiqué à l'étudiant relu.
- Le lien d'un exercice ne peut être remplacé que tant que sa relecture n'a pas commencé.
- Une relecture peut être modifiée tant que la session n'est pas clôturée, conformément à la décision retenue pour la contradiction Q10/Q15.