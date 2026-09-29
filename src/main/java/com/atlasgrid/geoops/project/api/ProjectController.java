package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.application.ProjectService;
import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.validation.ProjectValidationReport;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Command boundary for project intake and request validation.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectValidationService projectValidationService;

    public ProjectController(
            ProjectService projectService,
            ProjectValidationService projectValidationService
    ) {
        this.projectService = projectService;
        this.projectValidationService = projectValidationService;
    }

    @PostMapping("/validate")
    public ProjectValidationReport validateProject(
            @Valid @RequestBody CreateProjectRequest request
    ) {
        return projectValidationService.validate(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeoProject createProject(
            @Valid @RequestBody CreateProjectRequest request
    ) {
        return projectService.create(request);
    }
}
