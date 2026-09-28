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
    void rejectsUnsupportedDatasetType() throws IOException {
        Path dataset = Files.createFile(tempDir.resolve("survey.exe"));

        PreflightResult result = validator.validate(new String[]{dataset.toString()});

        assertThat(result.valid()).isFalse();
        assertThat(result.exitCode()).isEqualTo(5);
    }

    @Test
    void acceptsSupportedGeoJsonDataset() throws IOException {
        Path dataset = Files.createFile(tempDir.resolve("survey.geojson"));

        PreflightResult result = validator.validate(new String[]{dataset.toString()});

        assertThat(result.valid()).isTrue();
        assertThat(result.exitCode()).isZero();
    }
}
