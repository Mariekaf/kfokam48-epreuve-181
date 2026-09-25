package com.kfokam48.backend.exception;

import com.kfokam48.backend.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Les données fournies sont invalides.");

        ApiErrorResponse response = new ApiErrorResponse(
                "VALIDATION_ERROR",
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(PromotionInconnueException.class)
    public ResponseEntity<ApiErrorResponse> handlePromotionInconnue(
            PromotionInconnueException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "PROMOTION_INCONNUE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(CodeSessionInconnuException.class)
    public ResponseEntity<ApiErrorResponse> handleCodeSessionInconnu(
            CodeSessionInconnuException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "CODE_INCONNU",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(EtudiantInconnuException.class)
    public ResponseEntity<ApiErrorResponse> handleEtudiantInconnu(
            EtudiantInconnuException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "ETUDIANT_INCONNU",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(EtudiantHorsPromotionException.class)
    public ResponseEntity<ApiErrorResponse> handleEtudiantHorsPromotion(
            EtudiantHorsPromotionException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "ETUDIANT_HORS_PROMOTION",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(SessionInconnueException.class)
    public ResponseEntity<ApiErrorResponse> handleSessionInconnue(
            SessionInconnueException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "SESSION_INCONNUE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(LienInvalideException.class)
    public ResponseEntity<ApiErrorResponse> handleLienInvalide(
            LienInvalideException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "LIEN_INVALIDE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(PresenceDejaEnregistreeException.class)
    public ResponseEntity<ApiErrorResponse> handlePresenceDejaEnregistree(
            PresenceDejaEnregistreeException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "DEJA_PRESENT",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(ExerciceDejaDeposeException.class)
    public ResponseEntity<ApiErrorResponse> handleExerciceDejaDepose(
            ExerciceDejaDeposeException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "EXERCICE_DEJA_DEPOSE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(SessionClotureeException.class)
    public ResponseEntity<ApiErrorResponse> handleSessionCloturee(
            SessionClotureeException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "SESSION_CLOTUREE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(NoteInvalideException.class)
    public ResponseEntity<ApiErrorResponse> handleNoteInvalide(
            NoteInvalideException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "NOTE_INVALIDE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(AutoRelectureException.class)
    public ResponseEntity<ApiErrorResponse> handleAutoRelecture(
            AutoRelectureException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "AUTO_RELECTURE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(RelectureInconnueException.class)
    public ResponseEntity<ApiErrorResponse> handleRelectureInconnue(
            RelectureInconnueException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "RELECTURE_INCONNUE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(RelectureDejaRendueException.class)
    public ResponseEntity<ApiErrorResponse> handleRelectureDejaRendue(
            RelectureDejaRendueException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "RELECTURE_DEJA_RENDUE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(CodeSessionExpireException.class)
    public ResponseEntity<ApiErrorResponse> handleCodeSessionExpire(
            CodeSessionExpireException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "CODE_EXPIRE",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(response);
    }

    @ExceptionHandler(TentativesBloqueesException.class)
    public ResponseEntity<ApiErrorResponse> handleTentativesBloquees(
            TentativesBloqueesException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "TENTATIVES_BLOQUEES",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleJsonInvalide(
            HttpMessageNotReadableException exception
    ) {
        if (exception.getMessage() != null
                && exception.getMessage().contains("\"note\"")) {
            ApiErrorResponse response = new ApiErrorResponse(
                    "NOTE_INVALIDE",
                    "La note doit etre un entier compris entre 0 et 20."
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        ApiErrorResponse response = new ApiErrorResponse(
                "REQUETE_INVALIDE",
                "Le corps de la requête est invalide."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleErreurInterne(
            Exception exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                "ERREUR_INTERNE",
                "Une erreur interne est survenue."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
