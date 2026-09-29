package com.atlasgrid.geoops.tools.preflight;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DatasetPreflightValidatorTest {

    private final DatasetPreflightValidator validator = new DatasetPreflightValidator();

    @TempDir
    Path tempDir;

    @Test
    void returnsUsageErrorWhenNoDatasetWasProvided() {
        PreflightResult result = validator.validate(new String[0]);

        assertThat(result.valid()).isFalse();
        assertThat(result.exitCode()).isEqualTo(2);
    }

    @Test
    void returnsNotFoundWhenDatasetDoesNotExist() {
        PreflightResult result =
                validator.validate(new String[]{tempDir.resolve("missing.geojson").toString()});

        assertThat(result.valid()).isFalse();
        assertThat(result.exitCode()).isEqualTo(3);
    }

    @Test
    void rejectsUnsupportedDatasetTypeAndListsEnumFormats() throws IOException {
        Path dataset = Files.createFile(tempDir.resolve("survey.exe"));

        PreflightResult result = validator.validate(new String[]{dataset.toString()});

        assertThat(result.valid()).isFalse();
        assertThat(result.exitCode()).isEqualTo(5);
        assertThat(result.message()).contains(".csv, .json, .geojson");
    }

    @Test
    void handlesInvalidPathWithoutLeakingRuntimeException() {
        PreflightResult result =
                validator.validate(new String[]{"bad\u0000path.geojson"});

        assertThat(result.valid()).isFalse();
        assertThat(result.exitCode()).isEqualTo(6);
        assertThat(result.message())
                .isEqualTo("Dataset path is invalid or inaccessible");
    }

    @Test
    void acceptsEveryDatasetFormatDefinedByTheEnum() throws IOException {
        for (DatasetFormat format : DatasetFormat.values()) {
            Path dataset = Files.createFile(
                    tempDir.resolve("survey-" + format.name().toLowerCase() + format.extension())
            );

            PreflightResult result = validator.validate(new String[]{dataset.toString()});

            assertThat(result.valid()).isTrue();
            assertThat(result.exitCode()).isZero();
        }
    }
}
