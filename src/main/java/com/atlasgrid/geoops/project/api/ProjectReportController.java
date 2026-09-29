package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.application.ProjectCollectionSummary;
import com.atlasgrid.geoops.project.application.ProjectCollectionSummaryService;
import com.atlasgrid.geoops.project.application.ProjectCrsCatalogService;
import com.atlasgrid.geoops.project.application.ProjectManifestFormatter;
import com.atlasgrid.geoops.project.application.ProjectService;
import com.atlasgrid.geoops.project.application.ProjectSortingService;
import com.atlasgrid.geoops.project.application.ProjectSortingView;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only reporting/view boundary for derived project information.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectReportController {

    private final ProjectService projectService;
    private final ProjectManifestFormatter projectManifestFormatter;
    private final ProjectCollectionSummaryService projectCollectionSummaryService;
    private final ProjectCrsCatalogService projectCrsCatalogService;
    private final ProjectSortingService projectSortingService;

    public ProjectReportController(
            ProjectService projectService,
            ProjectManifestFormatter projectManifestFormatter,
            ProjectCollectionSummaryService projectCollectionSummaryService,
            ProjectCrsCatalogService projectCrsCatalogService,
            ProjectSortingService projectSortingService
    ) {
        this.projectService = projectService;
        this.projectManifestFormatter = projectManifestFormatter;
        this.projectCollectionSummaryService = projectCollectionSummaryService;
        this.projectCrsCatalogService = projectCrsCatalogService;
        this.projectSortingService = projectSortingService;
    }

    @GetMapping("/collection-summary")
    public ProjectCollectionSummary getProjectCollectionSummary() {
        return projectCollectionSummaryService
                .summarize(projectService.findAll());
    }

    @GetMapping("/sorting-preview")
    public ProjectSortingView getProjectSortingPreview() {
        return projectSortingService.sort(projectService.findAll());
    }

    @GetMapping("/crs-catalog")
    public List<String> getCoordinateReferenceSystemCatalog() {
        return projectCrsCatalogService
                .sortedUniqueCoordinateReferenceSystems(
                        projectService.findAll()
                );
    }

    @GetMapping(
            value = "/manifest",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String getProjectManifest() {
        return projectManifestFormatter.format(projectService.findAll());
    }
}
