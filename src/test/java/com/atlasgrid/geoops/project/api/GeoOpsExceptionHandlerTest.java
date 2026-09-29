package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GeoOpsExceptionHandlerTest {

    private final GeoOpsExceptionHandler handler =
            new GeoOpsExceptionHandler();

    @Test
    void unexpectedExceptionReturnsSafeInternalErrorWithoutLeakingMessage() {
        IllegalStateException exception =
                new IllegalStateException(
                        "internal database password or implementation detail"
                );

        ResponseEntity<ApiError> response =
                handler.handleUnexpectedException(exception);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code())
                .isEqualTo(GeoOpsErrorCode.INTERNAL_ERROR);
        assertThat(response.getBody().message())
                .isEqualTo("Unexpected server error");
        assertThat(response.getBody().message())
                .doesNotContain("database password");
    }
}
