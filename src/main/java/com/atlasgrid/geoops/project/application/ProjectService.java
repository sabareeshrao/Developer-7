package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.domain.ProjectCatalogSnapshot;
import com.atlasgrid.geoops.project.validation.ProjectValidationReport;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application/service layer for the GeoOps project-intake vertical slice.
 *
 * <p>Mutable collection ownership is delegated to ProjectCatalog. The service
 * works through the catalog abstraction instead of exposing or manipulating
 * concrete collection implementations directly.</p>
 *
 * <p>Expected lookup absence is represented with Optional instead of throwing
 * an exception. This keeps exceptions reserved for actual exceptional/domain
 * failure conditions rather than normal control flow.</p>
 *
 * <p>Project creation executes the same domain validation rules exposed by the
 * validation endpoint. Invalid business input raises
 * InvalidProjectRequestException before any project state is changed.</p>
 *
 * <p>Storage is intentionally in-memory. A later database-focused anchor will
 * replace this implementation with persistence when the learning sequence
 * reaches database integration.</p>
 */
@Slf4j
@Service
public class ProjectService {

    private final ProjectValidationService projectValidationService;
    private final ProjectCatalog projectCatalog;

    public ProjectService(
            ProjectValidationService projectValidationService,
            ProjectCatalog projectCatalog
    ) {
        this.projectValidationService = projectValidationService;
        this.projectCatalog = projectCatalog;
    }

    public List<GeoProject> findAll() {
        return projectCatalog.findAll();
    }

    public ProjectCatalogSnapshot catalogSnapshot() {
        return new ProjectCatalogSnapshot(Instant.now(), projectCatalog.findAll());
    }

    public Optional<GeoProject> findByProjectCode(String projectCode) {
        return projectCatalog.findByProjectCode(projectCode);
    }

    public boolean containsProjectCode(String projectCode) {
        return findByProjectCode(projectCode).isPresent();
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

        log.info("Created GeoOps project code={} crs={}",
                project.projectCode(), project.coordinateReferenceSystem());

        return project;
    }
}
