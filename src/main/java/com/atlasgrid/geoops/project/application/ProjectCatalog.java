package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.domain.ProjectIdentity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Owns the mutable in-memory collections used by the current GeoOps project
 * catalog.
 *
 * <p>The class programs to List and Set interfaces while choosing ArrayList
 * and HashSet for the current behavior. ArrayList is a good fit because project
 * intake is append-oriented, order matters, and the operations team can also
 * read a project efficiently by its intake position.</p>
 *
 * <p>The class never exposes its mutable collections directly; callers receive
 * immutable snapshots.</p>
 *
 * <p>ProjectIdentity is immutable, so values stored in the hash-based identity
 * Set cannot change their equality/hashCode state after insertion.</p>
 */
@Component
public class ProjectCatalog {

    private final List<GeoProject> projects = new ArrayList<>();
    private final Set<ProjectIdentity> projectIdentities = new HashSet<>();

    public boolean add(GeoProject project) {
        Objects.requireNonNull(project, "project");

        ProjectIdentity identity = new ProjectIdentity(project.projectCode());

        if (!projectIdentities.add(identity)) {
            return false;
        }

        projects.add(project);
        return true;
    }

    public List<GeoProject> findAll() {
        return List.copyOf(projects);
    }

    /**
     * Returns the most recently accepted projects while preserving their
     * original intake order within the returned window.
     */
    public List<GeoProject> findRecent(int limit) {
        if (limit <= 0 || projects.isEmpty()) {
            return List.of();
        }

        int fromIndex = Math.max(0, projects.size() - limit);

        return List.copyOf(
                projects.subList(fromIndex, projects.size())
        );
    }

    /**
     * Returns a project using a human-friendly, 1-based intake position.
     */
    public Optional<GeoProject> findByIntakePosition(int intakePosition) {
        if (intakePosition < 1 || intakePosition > projects.size()) {
            return Optional.empty();
        }

        return Optional.of(projects.get(intakePosition - 1));
    }

    public boolean containsProjectCode(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        return projectIdentities.contains(
                new ProjectIdentity(projectCode)
        );
    }

    public Optional<GeoProject> findByProjectCode(String projectCode) {
        return projects.stream()
                .filter(project -> project.projectCode().equals(projectCode))
                .findFirst();
    }

    public int size() {
        return projects.size();
    }
}
