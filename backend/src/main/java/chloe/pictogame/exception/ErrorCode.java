package chloe.pictogame.exception;

import chloe.pictogame.common.WordConstants;

public enum ErrorCode {
    INVALID_WORD_COUNT("count must be between %d and %d".formatted(WordConstants.MIN_COUNT, WordConstants.MAX_COUNT)),
    INVALID_LABEL_NULL("new word can't be null"),
    INVALID_LABEL_DUPLICATE("this word already exist"),
    INVALID_LABEL("invalid word"),
    INVALID_PARAMETER("invalid parameter"),
    DATABASE_UNAVAILABLE("database unavailable"),
    INTERNAL_ERROR("internal error");

    public final String message;

    ErrorCode(String message){
        this.message = message;
    }
}
