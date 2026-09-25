package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    boolean existsBySessionAndEtudiant(CourseSession session, Etudiant etudiant);
}
