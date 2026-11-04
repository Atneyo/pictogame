package chloe.pictogame.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

/**
 * Converts every exception into a uniform JSON error response (RFC 9457 ProblemDetail).
 * Extending ResponseEntityExceptionHandler already covers the standard Spring MVC errors
 * (404, 405, missing parameter...) with the same format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Our business exceptions: any subclass of ApiException is handled here
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemDetail> handleApiException(ApiException ex, HttpServletRequest request) {
        if (ex.getStatus().is5xxServerError()) {
            log.error("{} : {}", ex.getErrorCode(), ex.getMessage(), ex);
        } else {
            log.warn("{} : {}", ex.getErrorCode(), ex.getMessage());
        }
        return build(ex.getStatus(), ex.getErrorCode(), ex.getMessage(), request);
    }

    // Wrong parameter type, e.g. ?count=abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String expected = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String detail = "Parameter '%s' must be of type %s (received: '%s')"
                .formatted(ex.getName(), expected, ex.getValue());
        log.warn("{} : {}", ErrorCode.INVALID_PARAMETER, detail);
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, detail, request);
    }

    // Database stopped or unreachable
    @ExceptionHandler({DataAccessResourceFailureException.class, CannotCreateTransactionException.class})
    public ResponseEntity<ProblemDetail> handleDatabaseUnavailable(Exception ex, HttpServletRequest request) {
        log.error("Database unavailable", ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.DATABASE_UNAVAILABLE,
                "The database is currently unavailable, please try again later", request);
    }

    // Safety net for anything unexpected. The client gets a generic message,
    // the details stay in the logs only (never expose internals).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred", request);
    }

    private ResponseEntity<ProblemDetail> build(
            HttpStatus status, ErrorCode code, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", code.name());
        problem.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(problem);
    }
}