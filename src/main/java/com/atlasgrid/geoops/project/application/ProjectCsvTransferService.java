package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import org.apache.commons.csv.CSVException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Imports and exports the bounded CSV exchange format used by GeoOps.
 *
 * <p>Parsing and printing use Apache Commons CSV instead of a hand-written
 * parser so quoted commas, escaped quotes and multiline quoted values follow a
 * mature RFC-4180 implementation.</p>
 *
 * <p>Import is two-phase: the entire CSV document is parsed into requests
 * first, then ProjectService validates and publishes the whole batch
 * atomically. A bad later row therefore cannot leave earlier rows committed.</p>
 */
@Service
public class ProjectCsvTransferService {

    static final List<String> HEADERS = List.of(
            "projectCode",
            "name",
            "coordinateReferenceSystem"
    );

    private static final CSVFormat IMPORT_FORMAT =
            CSVFormat.RFC4180.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .get();

    private static final CSVFormat EXPORT_FORMAT =
            CSVFormat.RFC4180.builder()
                    .setHeader(HEADERS.toArray(String[]::new))
                    .get();

    private final ProjectService projectService;

    public ProjectCsvTransferService(ProjectService projectService) {
        this.projectService = Objects.requireNonNull(
                projectService,
                "projectService"
        );
    }

    public ProjectImportResult importCsv(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "inputStream");

        List<CreateProjectRequest> requests = parseRequests(inputStream);
        List<GeoProject> projects =
                projectService.createAllAtomically(requests);

        return new ProjectImportResult(
                projects.size(),
                projects.stream()
                        .map(GeoProject::projectCode)
                        .toList()
        );
    }

    public String exportCsv() {
        try {
            StringWriter writer = new StringWriter();

            try (CSVPrinter printer =
                         new CSVPrinter(writer, EXPORT_FORMAT)) {
                for (GeoProject project : projectService.findAll()) {
                    printer.printRecord(
                            safeForSpreadsheet(project.projectCode()),
                            safeForSpreadsheet(project.name()),
                            safeForSpreadsheet(
                                    project.coordinateReferenceSystem()
                            )
                    );
                }
            }

            return writer.toString();
        } catch (IOException exception) {
            throw new ProjectDataTransferIoException(
                    "Unable to generate CSV export data",
                    exception
            );
        }
    }

    private List<CreateProjectRequest> parseRequests(
            InputStream inputStream
    ) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                )
        )) {
            skipOptionalBom(reader);

            try (CSVParser parser = IMPORT_FORMAT.parse(reader)) {
                if (!HEADERS.equals(parser.getHeaderNames())) {
                    throw new ProjectDataTransferException(
                            "CSV header must be: "
                                    + String.join(",", HEADERS)
                    );
                }

                List<CreateProjectRequest> requests =
                        new ArrayList<>();

                for (CSVRecord record : parser) {
                    if (record.size() != HEADERS.size()) {
                        throw new ProjectDataTransferException(
                                "CSV record "
                                        + record.getRecordNumber()
                                        + " must contain exactly "
                                        + HEADERS.size()
                                        + " columns"
                        );
                    }

                    requests.add(new CreateProjectRequest(
                            restoreSpreadsheetEscape(
                                    record.get(HEADERS.get(0))
                            ).trim(),
                            restoreSpreadsheetEscape(
                                    record.get(HEADERS.get(1))
                            ).trim(),
                            restoreSpreadsheetEscape(
                                    record.get(HEADERS.get(2))
                            ).trim()
                    ));
                }

                return List.copyOf(requests);
            } catch (CSVException exception) {
                throw new ProjectDataTransferException(
                        "CSV content is malformed",
                        exception
                );
            }
        } catch (ProjectDataTransferException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new ProjectDataTransferIoException(
                    "Unable to read CSV import data",
                    exception
            );
        }
    }

    private static void skipOptionalBom(BufferedReader reader)
            throws IOException {
        reader.mark(1);
        int firstCharacter = reader.read();

        if (firstCharacter != 0xFEFF && firstCharacter != -1) {
            reader.reset();
        }
    }

    /**
     * Prevents spreadsheet applications from executing user-controlled CSV
     * cells as formulas. The import path understands and reverses this transport
     * escape so an export→import round trip preserves the logical value.
     */
    private static String safeForSpreadsheet(String value) {
        String safeValue = Objects.requireNonNull(value, "CSV value");

        int firstNonWhitespace = 0;
        while (firstNonWhitespace < safeValue.length()
                && Character.isWhitespace(
                        safeValue.charAt(firstNonWhitespace)
                )) {
            firstNonWhitespace++;
        }

        if (firstNonWhitespace < safeValue.length()
                && isFormulaPrefix(
                        safeValue.charAt(firstNonWhitespace)
                )) {
            return "'"
                    + safeValue.substring(0, firstNonWhitespace)
                    + safeValue.substring(firstNonWhitespace);
        }

        return safeValue;
    }

    private static String restoreSpreadsheetEscape(String value) {
        if (value.length() >= 2 && value.charAt(0) == 39) {
            int firstNonWhitespace = 1;

            while (firstNonWhitespace < value.length()
                    && Character.isWhitespace(
                            value.charAt(firstNonWhitespace)
                    )) {
                firstNonWhitespace++;
            }

            if (firstNonWhitespace < value.length()
                    && isFormulaPrefix(
                            value.charAt(firstNonWhitespace)
                    )) {
                return value.substring(1);
            }
        }

        return value;
    }

    private static boolean isFormulaPrefix(char value) {
        return value == '='
                || value == '+'
                || value == '-'
                || value == '@';
    }
}
