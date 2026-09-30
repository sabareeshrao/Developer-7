package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Writes the existing GeoOps project manifest to a file.
 */
@Component
public class ProjectManifestFileExporter {

    private final ProjectManifestFormatter projectManifestFormatter;

    public ProjectManifestFileExporter(
            ProjectManifestFormatter projectManifestFormatter
    ) {
        this.projectManifestFormatter = projectManifestFormatter;
    }

    public Path export(
            List<GeoProject> projects,
            Path target
    ) throws IOException {
        Objects.requireNonNull(projects, "projects");
        Objects.requireNonNull(target, "target");

        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                target,
                StandardCharsets.UTF_8
        )) {
            writer.write(
                    projectManifestFormatter.format(projects)
            );
        }

        return target;
    }
}
