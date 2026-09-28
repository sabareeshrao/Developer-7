package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.application.ProjectService;
import com.atlasgrid.geoops.project.domain.GeoProject;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST entry point for GIS project intake.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<GeoProject> getProjects() {
        return projectService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeoProject createProject(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }
}
