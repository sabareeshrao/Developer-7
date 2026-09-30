package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.domain.ProjectCatalogSnapshot;
import com.atlasgrid.geoops.project.validation.ProjectValidationReport;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;
import com.atlasgrid.geoops.project.validation.ProjectValidationStandards;
import com.atlasgrid.geoops.review.ProjectReviewQueue;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Application/service layer for the GeoOps project-intake vertical slice.
 *
 * <p>Required-field and domain validation live below MVC so every intake path
 * receives the same business rules. Accepted CRS identifiers are canonicalized
 * before storage.</p>
 *
 * <p>The in-memory intake lock coordinates catalog publication and review-queue
 * publication. It is not a substitute for a future database transaction, but
 * it prevents concurrent service calls from racing the current in-memory
 * catalog.</p>
 */
@Slf4j
@Service
public class ProjectService {

    private final Object intakeLock = new Object();

    private final ProjectValidationService projectValidationService;
    private final ProjectCatalog projectCatalog;
    private final ProjectReviewQueue projectReviewQueue;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring-managed mutable collaborators are intentionally referenced, not exposed."
    )
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
        return new ProjectCatalogSnapshot(
                Instant.now(),
                projectCatalog.findAll()
        );
    }

    public List<GeoProject> findRecent(int limit) {
        return projectCatalog.findRecent(limit);
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
        CreateProjectRequest canonicalRequest =
                validateAndCanonicalize(request);

        synchronized (intakeLock) {
            GeoProject project = toProject(canonicalRequest);

            if (!projectCatalog.add(project)) {
                throw new DuplicateProjectException(
                        canonicalRequest.projectCode()
                );
            }

            projectReviewQueue.enqueue(project.projectCode());

            log.info(
                    "Created GeoOps project code={} crs={} and queued for review",
                    project.projectCode(),
                    project.coordinateReferenceSystem()
            );

            return project;
        }
    }

    /**
     * Validates an entire batch before publishing any project.
     *
     * <p>If validation or duplicate detection fails, the method mutates neither
     * the catalog nor the review queue.</p>
     */
    public List<GeoProject> createAllAtomically(
            List<CreateProjectRequest> requests
    ) {
        Objects.requireNonNull(requests, "requests");

        List<CreateProjectRequest> canonicalRequests =
                requests.stream()
                        .map(this::validateAndCanonicalize)
                        .toList();

        rejectDuplicatesInsideBatch(canonicalRequests);

        synchronized (intakeLock) {
            for (CreateProjectRequest request : canonicalRequests) {
                if (projectCatalog.containsProjectCode(
                        request.projectCode()
                )) {
                    throw new DuplicateProjectException(
                            request.projectCode()
                    );
                }
            }

            List<GeoProject> projects = new ArrayList<>();

            for (CreateProjectRequest request : canonicalRequests) {
                projects.add(toProject(request));
            }

            projectCatalog.addAllAtomically(projects);
            projectReviewQueue.enqueueAll(
                    projects.stream()
                            .map(GeoProject::projectCode)
                            .toList()
            );

            log.info(
                    "Imported {} GeoOps projects and queued them for review",
                    projects.size()
            );

            return List.copyOf(projects);
        }
    }

    private CreateProjectRequest validateAndCanonicalize(
            CreateProjectRequest request
    ) {
        Objects.requireNonNull(request, "request");

        ProjectValidationReport validationReport =
                projectValidationService.validate(request);

        if (!validationReport.valid()) {
            throw new InvalidProjectRequestException(
                    validationReport.issues()
            );
        }

        return new CreateProjectRequest(
                request.projectCode(),
                request.name().trim(),
                ProjectValidationStandards.normalizeCrsIdentifier(
                        request.coordinateReferenceSystem()
                )
        );
    }

    private GeoProject toProject(CreateProjectRequest request) {
        return new GeoProject(
                UUID.randomUUID(),
                request.projectCode(),
                request.name(),
                request.coordinateReferenceSystem(),
                Instant.now()
        );
    }

    private void rejectDuplicatesInsideBatch(
            List<CreateProjectRequest> requests
    ) {
        Set<String> projectCodes = new HashSet<>();

        for (CreateProjectRequest request : requests) {
            if (!projectCodes.add(request.projectCode())) {
                throw new DuplicateProjectException(
                        request.projectCode()
                );
            }
        }
    }
}
