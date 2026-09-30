package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Imports and exports the small synchronous CSV exchange format used by GeoOps.
 *
 * <p>The import path is deliberately bounded and blocking. It is intended for
 * modest project-metadata files accepted by the REST endpoint, not very large
 * batch processing. Every imported row still goes through ProjectService so
 * existing validation, duplicate detection, catalog insertion and review-queue
 * behavior remain authoritative.</p>
 */
@Service
public class ProjectCsvTransferService {

    static final String HEADER =
            "projectCode,name,coordinateReferenceSystem";

    private final ProjectService projectService;

    public ProjectCsvTransferService(ProjectService projectService) {
        this.projectService = Objects.requireNonNull(
                projectService,
                "projectService"
        );
    }

    public ProjectImportResult importCsv(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "inputStream");

        List<String> importedCodes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        )) {
            String header = reader.readLine();

            if (header == null || !HEADER.equals(stripBom(header).trim())) {
                throw new ProjectDataTransferException(
                        "CSV header must be: " + HEADER
                );
            }

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                List<String> fields = parseCsvLine(line, lineNumber);

                if (fields.size() != 3) {
                    throw new ProjectDataTransferException(
                            "CSV line " + lineNumber
                                    + " must contain exactly 3 columns"
                    );
                }

                CreateProjectRequest request = new CreateProjectRequest(
                        fields.get(0).trim(),
                        fields.get(1).trim(),
                        fields.get(2).trim()
                );

                GeoProject project = projectService.create(request);
                importedCodes.add(project.projectCode());
            }
        } catch (IOException exception) {
            throw new ProjectDataTransferException(
                    "Unable to read CSV import data",
                    exception
            );
        }

        return new ProjectImportResult(
                importedCodes.size(),
                importedCodes
        );
    }

    public String exportCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append(HEADER).append(System.lineSeparator());

        for (GeoProject project : projectService.findAll()) {
            csv.append(escape(project.projectCode())).append(',')
                    .append(escape(project.name())).append(',')
                    .append(escape(project.coordinateReferenceSystem()))
                    .append(System.lineSeparator());
        }

        return csv.toString();
    }

    private static List<String> parseCsvLine(
            String line,
            int lineNumber
    ) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);

            if (quoted) {
                if (character == '"') {
                    if (index + 1 < line.length()
                            && line.charAt(index + 1) == '"') {
                        current.append('"');
                        index++;
                    } else {
                        quoted = false;
                    }
                } else {
                    current.append(character);
                }
            } else if (character == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else if (character == '"') {
                if (!current.isEmpty()) {
                    throw new ProjectDataTransferException(
                            "Unexpected quote in CSV line " + lineNumber
                    );
                }
                quoted = true;
            } else {
                current.append(character);
            }
        }

        if (quoted) {
            throw new ProjectDataTransferException(
                    "Unclosed quoted field in CSV line " + lineNumber
            );
        }

        fields.add(current.toString());
        return fields;
    }

    private static String escape(String value) {
        String safeValue = Objects.requireNonNull(value, "CSV value");

        if (safeValue.indexOf(',') >= 0
                || safeValue.indexOf('"') >= 0
                || safeValue.indexOf('\n') >= 0
                || safeValue.indexOf('\r') >= 0) {
            return "\"" + safeValue.replace("\"", "\"\"") + "\"";
        }

        return safeValue;
    }

    private static String stripBom(String value) {
        if (!value.isEmpty() && value.charAt(0) == '\uFEFF') {
            return value.substring(1);
        }
        return value;
    }
}
