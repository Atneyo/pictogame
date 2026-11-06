package chloe.pictogame.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();

        request = new MockHttpServletRequest();
        request.setRequestURI("/api/words");
    }

    @Test
    void handleApiException_shouldReturnProblemDetail() {
        // Prepare
        ApiException exception = mock(ApiException.class);

        when(exception.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
        when(exception.getErrorCode()).thenReturn(ErrorCode.INTERNAL_ERROR);
        when(exception.getMessage()).thenReturn("Word not found");

        // Execute
        ResponseEntity<ProblemDetail> response =
                handler.handleApiException(exception, request);

        // Control
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ProblemDetail body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(404);
        assertThat(body.getDetail()).isEqualTo("Word not found");
        assertThat(body.getInstance()).hasToString("/api/words");
        assertThat(body.getProperties())
                .containsEntry("code", ErrorCode.INTERNAL_ERROR.name())
                .containsKey("timestamp");

        assertThat(Instant.parse((String) body.getProperties().get("timestamp")))
                .isNotNull();
    }

    @Test
    void handleArgumentTypeMismatch_shouldReturnBadRequest() {
        // Prepare
        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException(
                        "abc",
                        Integer.class,
                        "count",
                        null,
                        null
                );

        // Execute
        ResponseEntity<ProblemDetail> response =
                handler.handleArgumentTypeMismatch(exception, request);

        // Control
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ProblemDetail body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getDetail())
                .isEqualTo("Parameter 'count' must be of type Integer (received: 'abc')");

        assertThat(body.getProperties())
                .containsEntry("code", ErrorCode.INVALID_PARAMETER.name())
                .containsKey("timestamp");
    }

    @Test
    void handleDatabaseUnavailable_shouldReturnServiceUnavailable() {
        // Prepare
        Exception exception =
                new DataAccessResourceFailureException("Database connection failed");

        // Execute
        ResponseEntity<ProblemDetail> response =
                handler.handleDatabaseUnavailable(exception, request);

        // Control
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        ProblemDetail body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(503);
        assertThat(body.getDetail())
                .isEqualTo("The database is currently unavailable, please try again later");

        assertThat(body.getProperties())
                .containsEntry("code", ErrorCode.DATABASE_UNAVAILABLE.name())
                .containsKey("timestamp");
    }

    @Test
    void handleDatabaseUnavailable_shouldAlsoHandleTransactionException() {
        // Prepare
        Exception exception =
                new CannotCreateTransactionException("Cannot create transaction");

        // Execute
        ResponseEntity<ProblemDetail> response =
                handler.handleDatabaseUnavailable(exception, request);

        // Control
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(503);
        assertThat(response.getBody().getProperties())
                .containsEntry("code", ErrorCode.DATABASE_UNAVAILABLE.name());
    }

    @Test
    void handleUnexpected_shouldReturnInternalServerError() {
        // Prepare
        Exception exception = new RuntimeException("Sensitive internal information");

        // Execute
        ResponseEntity<ProblemDetail> response =
                handler.handleUnexpected(exception, request);

        // Control
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ProblemDetail body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(500);
        assertThat(body.getDetail())
                .isEqualTo("An unexpected error occurred");

        // Internal exception details must not be exposed to the client.
        assertThat(body.getDetail())
                .doesNotContain("Sensitive internal information");

        assertThat(body.getProperties())
                .containsEntry("code", ErrorCode.INTERNAL_ERROR.name())
                .containsKey("timestamp");
    }
}