package chloe.pictogame.exception;

import org.springframework.http.HttpStatus;

public class InvalidWordCountException extends ApiException {

    public InvalidWordCountException(int count, int min, int max) {
        super(HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_WORD_COUNT,
                "count must be between %d and %d (received: %d)".formatted(min, max, count));
    }
}