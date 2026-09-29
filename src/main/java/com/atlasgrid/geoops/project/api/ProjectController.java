package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.application.ProjectCollectionSummary;
import com.atlasgrid.geoops.project.application.ProjectCollectionSummaryService;
import com.atlasgrid.geoops.project.application.ProjectManifestFormatter;
import com.atlasgrid.geoops.project.application.ProjectService;
import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.domain.ProjectCatalogSnapshot;
import com.atlasgrid.geoops.project.validation.ProjectValidationReport;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST entry point for GIS project intake.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectManifestFormatter projectManifestFormatter;
    private final ProjectValidationService projectValidationService;
    private final ProjectCollectionSummaryService projectCollectionSummaryService;

    public ProjectController(
            ProjectService projectService,
            ProjectManifestFormatter projectManifestFormatter,
            ProjectValidationService projectValidationService,
            ProjectCollectionSummaryService projectCollectionSummaryService
    ) {
        this.projectService = projectService;
        this.projectManifestFormatter = projectManifestFormatter;
        this.projectValidationService = projectValidationService;
        this.projectCollectionSummaryService = projectCollectionSummaryService;
    }

    @GetMapping
    public List<GeoProject> getProjects() {
        return projectService.findAll();
    }

    @GetMapping("/snapshot")
    public ProjectCatalogSnapshot getProjectCatalogSnapshot() {
        return projectService.catalogSnapshot();
    }

    @GetMapping("/collection-summary")
    public ProjectCollectionSummary getProjectCollectionSummary() {
        return projectCollectionSummaryService.summarize(projectService.findAll());
    }

    @GetMapping(value = "/manifest", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getProjectManifest() {
        return projectManifestFormatter.format(projectService.findAll());
    }

    @GetMapping("/exists/{projectCode}")
    public boolean projectCodeExists(@PathVariable String projectCode) {
        return projectService.containsProjectCode(projectCode);
    }

    @GetMapping("/by-position/{intakePosition}")
    public ResponseEntity<GeoProject> getProjectByIntakePosition(
            @PathVariable int intakePosition
    ) {
        return ResponseEntity.of(
                projectService.findByIntakePosition(intakePosition)
        );
    }

    @GetMapping("/by-code/{projectCode}")
    public ResponseEntity<GeoProject> getProjectByCode(
            @PathVariable String projectCode
    ) {
        return ResponseEntity.of(projectService.findByProjectCode(projectCode));
    }

    @PostMapping("/validate")
    public ProjectValidationReport validateProject(
            @Valid @RequestBody CreateProjectRequest request
    ) {
        return projectValidationService.validate(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeoProject createProject(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }
}
