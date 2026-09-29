package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.domain.ProjectCatalogSnapshot;
import com.atlasgrid.geoops.project.domain.ProjectIdentity;
import com.atlasgrid.geoops.project.validation.ProjectValidationReport;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Application/service layer for the GeoOps project-intake vertical slice.
 *
 * <p>The {@code projects} field is a final reference: the service cannot
 * reassign it to a different List after construction. The ArrayList itself is
 * still mutable, so create(...) can add projects. findAll() returns a defensive
 * immutable snapshot so callers cannot mutate the service's internal list.</p>
 *
 * <p>The {@code projectIdentities} HashSet uses ProjectIdentity.equals() and
 * hashCode() to detect a duplicate logical project code before creating a new
 * project record.</p>
 *
 * <p>Project-code lookup uses String.equals(...) because an incoming request or
 * path value can be a different String object with the same text. Reference
 * equality with == would not be a valid business comparison.</p>
 *
 * <p>Expected lookup absence is represented with Optional instead of throwing
 * an exception. This keeps exceptions reserved for actual exceptional/domain
 * failure conditions rather than normal control flow.</p>
 *
 * <p>Project creation executes the same domain validation rules exposed by the
 * validation endpoint. Invalid business input raises
 * InvalidProjectRequestException before any project state is changed.</p>
 *
 * <p>catalogSnapshot() returns an immutable ProjectCatalogSnapshot that captures
 * the current project list using a defensive copy.</p>
 *
 * <p>Storage is intentionally in-memory. A later database-focused anchor will
 * replace this implementation with persistence when the learning sequence
 * reaches database integration.</p>
 */
@Slf4j
@Service
public class ProjectService {

    private final ProjectValidationService projectValidationService;
    private final List<GeoProject> projects = new ArrayList<>();
    private final Set<ProjectIdentity> projectIdentities = new HashSet<>();

    public ProjectService(ProjectValidationService projectValidationService) {
        this.projectValidationService = projectValidationService;
    }

    public List<GeoProject> findAll() {
        return List.copyOf(projects);
    }

    public ProjectCatalogSnapshot catalogSnapshot() {
        return new ProjectCatalogSnapshot(Instant.now(), projects);
    }

    public Optional<GeoProject> findByProjectCode(String projectCode) {
        return projects.stream()
                .filter(project -> project.projectCode().equals(projectCode))
                .findFirst();
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

        ProjectIdentity identity = new ProjectIdentity(request.projectCode());

        if (!projectIdentities.add(identity)) {
            throw new DuplicateProjectException(request.projectCode());
        }

        GeoProject project = new GeoProject(
                UUID.randomUUID(),
                request.projectCode(),
                request.name(),
                request.coordinateReferenceSystem(),
                Instant.now()
        );

        projects.add(project);
        log.info("Created GeoOps project code={} crs={}",
                project.projectCode(), project.coordinateReferenceSystem());

        return project;
    }
}
