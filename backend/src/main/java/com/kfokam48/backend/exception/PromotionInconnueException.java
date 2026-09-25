package com.kfokam48.backend.exception;

public class PromotionInconnueException extends RuntimeException {

    public PromotionInconnueException(Long promotionId) {
        super("La promotion " + promotionId + " est inconnue.");
    }
}