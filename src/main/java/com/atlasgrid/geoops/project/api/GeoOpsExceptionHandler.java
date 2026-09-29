package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.application.DuplicateProjectException;
import com.atlasgrid.geoops.project.application.InvalidProjectRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

/**
 * Central REST exception mapping for the GeoOps project API.
 *
 * <p>Application exceptions remain independent from HTTP concerns. This advice
 * translates them into stable API responses at the web boundary.</p>
 */
@RestControllerAdvice
public class GeoOpsExceptionHandler {

    @ExceptionHandler(DuplicateProjectException.class)
    public ResponseEntity<ApiError> handleDuplicateProject(
            DuplicateProjectException exception
    ) {
        return build(
                HttpStatus.CONFLICT,
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
                exception.getMessage(),
                details
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
                "Request failed API validation",
                details
        );
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            List<String> details
    ) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );

        return ResponseEntity.status(status).body(error);
    }
}
