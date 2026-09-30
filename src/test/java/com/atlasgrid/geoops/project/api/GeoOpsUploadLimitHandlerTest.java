package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;

class GeoOpsUploadLimitHandlerTest {

    private final GeoOpsExceptionHandler handler =
            new GeoOpsExceptionHandler();

    @Test
    void oversizedUploadMapsToPayloadTooLarge() {
        ResponseEntity<ApiError> response =
                handler.handleMaxUploadSizeExceeded(
                        new MaxUploadSizeExceededException(5_000_000)
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code())
                .isEqualTo(GeoOpsErrorCode.REQUEST_TOO_LARGE);
    }
}
