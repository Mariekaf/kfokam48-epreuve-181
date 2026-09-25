package com.kfokam48.backend.exception;

public class PromotionTableauInconnueException extends RuntimeException {

    public PromotionTableauInconnueException(Long promotionId) {
        super("La promotion " + promotionId + " est inconnue.");
    }
}