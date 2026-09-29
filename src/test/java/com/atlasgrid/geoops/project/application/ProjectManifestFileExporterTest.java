package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectManifestFileExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void exportsManifestWithoutUsingManualFinallyForCleanup() throws Exception {
        ProjectManifestFileExporter exporter =
                new ProjectManifestFileExporter(new ProjectManifestFormatter());

        GeoProject project = new GeoProject(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "TX-AUS-019",
                "Finally Safety Survey",
                "EPSG:4326",
                Instant.parse("2026-09-29T12:00:00Z")
        );

        Path target = tempDir.resolve("exports/project-manifest.txt");

        Path result = exporter.export(List.of(project), target);

        assertThat(result).isEqualTo(target);
        assertThat(Files.readString(target))
                .contains("GEOOPS PROJECT MANIFEST")
                .contains("projectCode=TX-AUS-019")
                .contains("crs=EPSG:4326");
    }
}
