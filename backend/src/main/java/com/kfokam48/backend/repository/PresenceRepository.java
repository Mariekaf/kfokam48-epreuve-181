package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.CourseSession;
import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    boolean existsBySessionAndEtudiant(CourseSession session, Etudiant etudiant);

    @Query("""
            select distinct p.etudiant
            from Presence p
            where p.session = :session
            and p.etudiant <> :auteur
            """)
    List<Etudiant> findEtudiantsPresentsEligibles(
            @Param("session") CourseSession session,
            @Param("auteur") Etudiant auteur
    );
}
