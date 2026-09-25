package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.TentativePresence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TentativePresenceRepository
        extends JpaRepository<TentativePresence, Long> {

    Optional<TentativePresence> findByEtudiant(Etudiant etudiant);
}
