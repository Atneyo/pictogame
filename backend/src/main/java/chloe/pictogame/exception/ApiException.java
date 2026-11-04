package chloe.pictogame.exception;

import org.springframework.http.HttpStatus;

/**
 * The parent of all business exceptions: each one carries its own HTTP status and code.
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;

    protected ApiException(HttpStatus status, ErrorCode errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}