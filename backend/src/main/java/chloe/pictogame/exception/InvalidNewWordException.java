package chloe.pictogame.exception;

import org.springframework.http.HttpStatus;

public class InvalidNewWordException extends ApiException {
    public InvalidNewWordException(String label) {
        super(HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_LABEL,
                "label must not be null (received: %s)".formatted(label));
    }
    public InvalidNewWordException(String label, ErrorCode code){
        super(HttpStatus.BAD_REQUEST,
                code,
                "%s (received: %s)".formatted(code.message,label));
    }
}