package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.Etudiant;
import com.kfokam48.backend.entity.Exercice;
import com.kfokam48.backend.entity.Relecture;
import com.kfokam48.backend.entity.RelectureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.List;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    boolean existsByExercice(Exercice exercice);

    Optional<Relecture> findByExercice(Exercice exercice);

    List<Relecture> findByRelecteurOrderByAffecteeAtDesc(Etudiant relecteur);

    long countByExercice(Exercice exercice);

    long countByRelecteurAndStatutIn(
            Etudiant relecteur,
            Collection<RelectureStatus> statuts
    );

    @Query("""
            select avg(r.note)
            from Relecture r
            where r.exercice.etudiant = :etudiant
              and r.statut = :statut
              and r.note is not null
            """)
    Double calculerMoyenneNotesRecues(
            @Param("etudiant") Etudiant etudiant,
            @Param("statut") RelectureStatus statut
    );
}