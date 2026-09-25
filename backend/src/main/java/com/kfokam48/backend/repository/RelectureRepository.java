package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.Relecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    boolean existsByExercice(Exercice exercice);

    Optional<Relecture> findByExercice(Exercice exercice);

    long countByExercice(Exercice exercice);
}
