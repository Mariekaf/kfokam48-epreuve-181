package com.kfokam48.backend.service;

import com.kfokam48.backend.dto.SubmitRelectureRequest;
import com.kfokam48.backend.entity.Relecture;
import com.kfokam48.backend.entity.ExerciceStatus;
import com.kfokam48.backend.entity.RelectureStatus;
import com.kfokam48.backend.exception.AutoRelectureException;
import com.kfokam48.backend.exception.NoteInvalideException;
import com.kfokam48.backend.exception.RelectureDejaRendueException;
import com.kfokam48.backend.exception.RelectureInconnueException;
import com.kfokam48.backend.repository.RelectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class RelectureService {

    private final RelectureRepository relectureRepository;
    private final Clock clock;

    public RelectureService(
            RelectureRepository relectureRepository,
            Clock clock
    ) {
        this.relectureRepository = relectureRepository;
        this.clock = clock;
    }

    @Transactional
    public void rendreRelecture(Long relectureId, SubmitRelectureRequest request) {

        validerNote(request.note());

        Relecture relecture = relectureRepository.findById(relectureId)
                .orElseThrow(() ->
                        new RelectureInconnueException(relectureId)
                );

        if (Objects.equals(
                relecture.getRelecteur().getId(),
                relecture.getExercice().getEtudiant().getId()
        )) {
            throw new AutoRelectureException();
        }

        if (com.kfokam48.backend.entity.SessionStatus.CLOTUREE.equals(
                relecture.getExercice().getSession().getStatut())) {
            throw new com.kfokam48.backend.exception.SessionClotureeException(
                    relecture.getExercice().getSession().getId());
        }

        if (RelectureStatus.RELU.equals(relecture.getStatut())) {
            throw new RelectureDejaRendueException();
        }

        relecture.setNote(request.note());
        relecture.setCommentaire(request.commentaire().trim());
        relecture.setRendueAt(OffsetDateTime.now(clock));
        relecture.setStatut(RelectureStatus.RELU);
        relecture.getExercice().setStatut(ExerciceStatus.RELU);

        relectureRepository.save(relecture);
    }

    private void validerNote(Integer note) {

        if (note == null || note < 0 || note > 20) {
            throw new NoteInvalideException();
        }
    }
}
