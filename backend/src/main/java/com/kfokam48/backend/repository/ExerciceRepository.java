package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    boolean existsBySessionAndEtudiant(CourseSession session, Etudiant etudiant);

    Optional<Exercice> findBySessionAndEtudiant(
            CourseSession session,
            Etudiant etudiant
    );

    long countByEtudiant(Etudiant etudiant);

    List<Exercice> findByEtudiantOrderByDeposeAtDesc(Etudiant etudiant);

    List<Exercice> findBySession(CourseSession session);
}