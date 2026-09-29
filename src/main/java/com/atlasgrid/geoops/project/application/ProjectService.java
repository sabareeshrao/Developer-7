package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.domain.ProjectCatalogSnapshot;
import com.atlasgrid.geoops.project.validation.ProjectValidationReport;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;
import com.atlasgrid.geoops.review.ProjectReviewQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application/service layer for the GeoOps project-intake vertical slice.
 *
 * <p>Mutable collection ownership is delegated to ProjectCatalog. Newly
 * accepted projects are also appended to ProjectReviewQueue so they enter the
 * quality-review stage in the same order they were accepted.</p>
 *
 * <p>Storage and review-queue state are intentionally in-memory. Later database
 * and concurrency anchors can evolve those implementations without changing
 * the current learning sequence prematurely.</p>
 */
@Slf4j
@Service
public class ProjectService {

    private final ProjectValidationService projectValidationService;
    private final ProjectCatalog projectCatalog;
    private final ProjectReviewQueue projectReviewQueue;

    public ProjectService(
            ProjectValidationService projectValidationService,
            ProjectCatalog projectCatalog,
            ProjectReviewQueue projectReviewQueue
    ) {
        this.projectValidationService = projectValidationService;
        this.projectCatalog = projectCatalog;
        this.projectReviewQueue = projectReviewQueue;
    }

    public List<GeoProject> findAll() {
        return projectCatalog.findAll();
    }

    public ProjectCatalogSnapshot catalogSnapshot() {
        return new ProjectCatalogSnapshot(Instant.now(), projectCatalog.findAll());
    }

    public Optional<GeoProject> findByIntakePosition(int intakePosition) {
        return projectCatalog.findByIntakePosition(intakePosition);
    }

    public Optional<GeoProject> findByProjectCode(String projectCode) {
        return projectCatalog.findByProjectCode(projectCode);
    }

    public boolean containsProjectCode(String projectCode) {
        return projectCatalog.containsProjectCode(projectCode);
    }

    public GeoProject create(CreateProjectRequest request) {
        ProjectValidationReport validationReport =
                projectValidationService.validate(request);

        if (!validationReport.valid()) {
            throw new InvalidProjectRequestException(validationReport.issues());
        }

        GeoProject project = new GeoProject(
                UUID.randomUUID(),
                request.projectCode(),
                request.name(),
                request.coordinateReferenceSystem(),
                Instant.now()
        );

        if (!projectCatalog.add(project)) {
            throw new DuplicateProjectException(request.projectCode());
        }

        projectReviewQueue.enqueue(project.projectCode());

        log.info("Created GeoOps project code={} crs={} and queued for review",
                project.projectCode(), project.coordinateReferenceSystem());

        return project;
    }
}
