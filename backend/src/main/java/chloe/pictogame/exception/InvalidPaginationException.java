package chloe.pictogame.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaginationException extends ApiException {
    public InvalidPaginationException(String message) {
        super(HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_PARAMETER,
                "pagination_error: %s".formatted(message));
    }
}