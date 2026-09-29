package com.atlasgrid.geoops.project.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Immutable read-only snapshot of the GeoOps project catalog at one point in time.
 *
 * <p>The class is final, all fields are private final, there are no setters,
 * and the mutable List input is defensively copied with List.copyOf(...).</p>
 *
 * <p>Standard JavaBean getters are also provided so Spring/Jackson can expose
 * this immutable domain object reliably as JSON without requiring field
 * visibility or Jackson-specific annotations on the domain type.</p>
 */
public final class ProjectCatalogSnapshot {

    private final Instant capturedAt;
    private final List<GeoProject> projects;

    public ProjectCatalogSnapshot(Instant capturedAt, List<GeoProject> projects) {
        this.capturedAt = Objects.requireNonNull(capturedAt, "capturedAt");
        this.projects = List.copyOf(Objects.requireNonNull(projects, "projects"));
    }

    public Instant capturedAt() {
        return capturedAt;
    }

    public List<GeoProject> projects() {
        return projects;
    }

    public int projectCount() {
        return projects.size();
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public List<GeoProject> getProjects() {
        return projects;
    }

    public int getProjectCount() {
        return projects.size();
    }
}
