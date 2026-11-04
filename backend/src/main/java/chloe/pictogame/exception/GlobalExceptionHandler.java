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
 * Transforme toutes les exceptions en réponses JSON uniformes.
 * Étendre ResponseEntityExceptionHandler gère déjà les erreurs standard de Spring MVC
 * (404, 405, paramètre manquant...) au même format ProblemDetail.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Nos exceptions métier (InvalidWordCountException, ...)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemDetail> handleApiException(ApiException ex, HttpServletRequest request) {
        if (ex.getStatus().is5xxServerError()) {
            log.error("{} : {}", ex.getErrorCode(), ex.getMessage(), ex);
        } else {
            log.warn("{} : {}", ex.getErrorCode(), ex.getMessage());
        }
        return build(ex.getStatus(), ex.getErrorCode(), ex.getMessage(), request);
    }

    // Ex. ?count=abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String expected = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String detail = "Parameter '%s' must be of type %s (received: '%s')"
                .formatted(ex.getName(), expected, ex.getValue());
        log.warn("{} : {}", ErrorCode.INVALID_PARAMETER, detail);
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, detail, request);
    }

    // MySQL arrêté ou injoignable
    @ExceptionHandler({DataAccessResourceFailureException.class, CannotCreateTransactionException.class})
    public ResponseEntity<ProblemDetail> handleDatabaseUnavailable(Exception ex, HttpServletRequest request) {
        log.error("Database unavailable", ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.DATABASE_UNAVAILABLE,
                "The database is currently unavailable, please try again later", request);
    }

    // Filet de sécurité : tout ce qui n'est pas prévu
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