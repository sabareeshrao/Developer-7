package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;
import com.atlasgrid.geoops.project.validation.ValidationIssue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectExceptionHierarchyTest {

    @Test
    void customProjectExceptionsShareOneGeoOpsBaseType() {
        GeoOpsProjectException duplicate =
                new DuplicateProjectException("TX-AUS-001");

        GeoOpsProjectException invalid =
                new InvalidProjectRequestException(
                        List.of(new ValidationIssue(
                                "PROJECT_CODE_FORMAT",
                                "Invalid project code"
                        ))
                );

        assertThat(duplicate)
                .isInstanceOf(RuntimeException.class)
                .isInstanceOf(GeoOpsProjectException.class);

        assertThat(invalid)
                .isInstanceOf(RuntimeException.class)
                .isInstanceOf(GeoOpsProjectException.class);

        assertThat(duplicate.errorCode())
                .isEqualTo(GeoOpsErrorCode.PROJECT_DUPLICATE);

        assertThat(invalid.errorCode())
                .isEqualTo(GeoOpsErrorCode.PROJECT_VALIDATION_FAILED);
    }

    @Test
    void baseExceptionSupportsExceptionChainingForFutureWrappers() {
        IllegalStateException cause =
                new IllegalStateException("Downstream processing failed");

        GeoOpsProjectException wrapped =
                new TestProjectException(
                        GeoOpsErrorCode.PROJECT_VALIDATION_FAILED,
                        "Project processing failed",
                        cause
                );

        assertThat(wrapped.getCause()).isSameAs(cause);
        assertThat(wrapped.errorCode())
                .isEqualTo(GeoOpsErrorCode.PROJECT_VALIDATION_FAILED);
    }

    private static final class TestProjectException
            extends GeoOpsProjectException {

        private TestProjectException(
                GeoOpsErrorCode errorCode,
                String message,
                Throwable cause
        ) {
            super(errorCode, message, cause);
        }
    }
}
