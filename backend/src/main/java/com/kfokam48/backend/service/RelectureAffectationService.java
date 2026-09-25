package com.kfokam48.backend.service;

import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.Relecture;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.repository.PresenceRepository;
import com.kfokam48.backend.repository.RelectureRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RelectureAffectationService {

    private final PresenceRepository presenceRepository;
    private final RelectureRepository relectureRepository;
    private final RelecteurSelector relecteurSelector;
    private final Clock clock;

    public RelectureAffectationService(
            PresenceRepository presenceRepository,
            RelectureRepository relectureRepository,
            RelecteurSelector relecteurSelector,
            Clock clock
    ) {
        this.presenceRepository = presenceRepository;
        this.relectureRepository = relectureRepository;
        this.relecteurSelector = relecteurSelector;
        this.clock = clock;
    }

    public void affecterRelecteurSiPossible(Exercice exercice) {

        if (relectureRepository.existsByExercice(exercice)) {
            return;
        }

        List<Etudiant> etudiantsEligibles =
                presenceRepository.findEtudiantsPresentsEligibles(
                        exercice.getSession(),
                        exercice.getEtudiant()
                );

        relecteurSelector.choisir(etudiantsEligibles)
                .ifPresent(relecteur -> creerRelecture(exercice, relecteur));
    }

    private void creerRelecture(Exercice exercice, Etudiant relecteur) {

        Relecture relecture = new Relecture();
        relecture.setExercice(exercice);
        relecture.setRelecteur(relecteur);
        relecture.setStatut(RelectureStatus.A_FAIRE);
        relecture.setAffecteeAt(OffsetDateTime.now(clock));

        relectureRepository.save(relecture);
    }
}
