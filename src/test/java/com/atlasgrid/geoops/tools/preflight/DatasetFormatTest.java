package com.atlasgrid.geoops.tools.preflight;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DatasetFormatTest {

    @Test
    void enumConstantsOwnTheirFileExtensions() {
        assertThat(DatasetFormat.CSV.extension()).isEqualTo(".csv");
        assertThat(DatasetFormat.JSON.extension()).isEqualTo(".json");
        assertThat(DatasetFormat.GEOJSON.extension()).isEqualTo(".geojson");
    }

    @Test
    void detectsSupportedDatasetExtensionsWithoutRawStringSet() {
        assertThat(DatasetFormat.supports(Path.of("survey.csv"))).isTrue();
        assertThat(DatasetFormat.supports(Path.of("survey.JSON"))).isTrue();
        assertThat(DatasetFormat.supports(Path.of("survey.geojson"))).isTrue();
        assertThat(DatasetFormat.supports(Path.of("survey.exe"))).isFalse();
    }

    @Test
    void reportsSupportedExtensionsFromEnumValues() {
        assertThat(DatasetFormat.supportedExtensions())
                .isEqualTo(".csv, .json, .geojson");
    }
}
