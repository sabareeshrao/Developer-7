package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;
import com.atlasgrid.geoops.project.application.DuplicateProjectException;
import com.atlasgrid.geoops.project.application.GeoOpsProjectException;
import com.atlasgrid.geoops.project.application.InvalidProjectRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

/**
 * Central REST exception mapping for the GeoOps project API.
 *
 * <p>Known exceptions are translated into specific client-safe responses.
 * Unexpected exceptions are logged with their stack trace and returned to
 * clients as a generic HTTP 500 response so internal implementation details are
 * not leaked through the API.</p>
 */
@Slf4j
@RestControllerAdvice
public class GeoOpsExceptionHandler {

    @ExceptionHandler(DuplicateProjectException.class)
    public ResponseEntity<ApiError> handleDuplicateProject(
            DuplicateProjectException exception
    ) {
        return build(
                HttpStatus.CONFLICT,
                exception.errorCode(),
                exception.getMessage(),
                List.of()
        );
    }

    @ExceptionHandler(InvalidProjectRequestException.class)
    public ResponseEntity<ApiError> handleInvalidProjectRequest(
            InvalidProjectRequestException exception
    ) {
        List<String> details = exception.issues().stream()
                .map(issue -> issue.ruleCode() + ": " + issue.message())
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                exception.errorCode(),
                exception.getMessage(),
                details
        );
    }

    @ExceptionHandler(GeoOpsProjectException.class)
    public ResponseEntity<ApiError> handleProjectException(
            GeoOpsProjectException exception
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                exception.errorCode(),
                exception.getMessage(),
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBeanValidation(
            MethodArgumentNotValidException exception
    ) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                GeoOpsErrorCode.REQUEST_VALIDATION_FAILED,
                "Request failed API validation",
                details
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedRequest(
            HttpMessageNotReadableException exception
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                GeoOpsErrorCode.REQUEST_MALFORMED,
                "Request body is malformed or unreadable",
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception
    ) {
        log.error("Unhandled exception while processing GeoOps request", exception);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                GeoOpsErrorCode.INTERNAL_ERROR,
                "Unexpected server error",
                List.of()
        );
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            GeoOpsErrorCode code,
            String message,
            List<String> details
    ) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                details
        );

        return ResponseEntity.status(status).body(error);
    }
}
