package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.application.ProjectCsvTransferService;
import com.atlasgrid.geoops.project.application.ProjectDataTransferException;
import com.atlasgrid.geoops.project.application.ProjectDataTransferIoException;
import com.atlasgrid.geoops.project.application.ProjectImportResult;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * REST boundary for bounded CSV project import/export.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectDataTransferController {

    private static final MediaType CSV =
            new MediaType("text", "csv", StandardCharsets.UTF_8);

    private final ProjectCsvTransferService transferService;

    public ProjectDataTransferController(
            ProjectCsvTransferService transferService
    ) {
        this.transferService = transferService;
    }

    @PostMapping(
            path = "/imports/csv",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectImportResult importCsv(
            @RequestPart("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            throw new ProjectDataTransferException(
                    "CSV import file must not be empty"
            );
        }

        try (InputStream inputStream = file.getInputStream()) {
            return transferService.importCsv(inputStream);
        } catch (IOException exception) {
            throw new ProjectDataTransferIoException(
                    "Unable to open uploaded CSV file",
                    exception
            );
        }
    }

    @GetMapping(
            path = "/exports/csv",
            produces = "text/csv"
    )
    public ResponseEntity<String> exportCsv() {
        String csv = transferService.exportCsv();

        return ResponseEntity.ok()
                .contentType(CSV)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("geoops-projects.csv")
                                .build()
                                .toString()
                )
                .body(csv);
    }
}
