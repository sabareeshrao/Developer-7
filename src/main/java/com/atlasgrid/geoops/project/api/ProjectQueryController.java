package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.application.ProjectService;
import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.domain.ProjectCatalogSnapshot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read/query boundary for the project catalog.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectQueryController {

    private final ProjectService projectService;

    public ProjectQueryController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<GeoProject> getProjects() {
        return projectService.findAll();
    }

    @GetMapping("/snapshot")
    public ProjectCatalogSnapshot getProjectCatalogSnapshot() {
        return projectService.catalogSnapshot();
    }

    @GetMapping("/recent")
    public List<GeoProject> getRecentProjects(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return projectService.findRecent(limit);
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
        return ResponseEntity.of(
                projectService.findByProjectCode(projectCode)
        );
    }
}
