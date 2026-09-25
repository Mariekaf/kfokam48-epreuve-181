package com.kfokam48.backend.service;

import com.kfokam48.backend.entity.Etudiant;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Component
public class RelecteurSelector {

    private final SecureRandom secureRandom = new SecureRandom();

    public Optional<Etudiant> choisir(List<Etudiant> etudiantsEligibles) {

        if (etudiantsEligibles.isEmpty()) {
            return Optional.empty();
        }

        int index = secureRandom.nextInt(etudiantsEligibles.size());

        return Optional.of(etudiantsEligibles.get(index));
    }
}
