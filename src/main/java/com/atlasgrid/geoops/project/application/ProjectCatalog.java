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
 * Thread-safe owner of the current in-memory GeoOps project catalog.
 *
 * <p>The implementation deliberately keeps ArrayList and HashSet because their
 * semantics were established by earlier learning Sets. Synchronization stays
 * inside this owner so callers never coordinate the collections themselves.</p>
 */
@Component
public class ProjectCatalog {

    private final List<GeoProject> projects = new ArrayList<>();
    private final Set<ProjectIdentity> projectIdentities = new HashSet<>();

    public synchronized boolean add(GeoProject project) {
        Objects.requireNonNull(project, "project");

        ProjectIdentity identity = new ProjectIdentity(project.projectCode());

        if (!projectIdentities.add(identity)) {
            return false;
        }

        projects.add(project);
        return true;
    }

    /**
     * Adds a whole batch or none of it.
     *
     * <p>Both conflicts with the existing catalog and duplicates inside the
     * incoming batch are detected before either backing collection is mutated.</p>
     */
    public synchronized void addAllAtomically(List<GeoProject> newProjects) {
        Objects.requireNonNull(newProjects, "newProjects");

        List<GeoProject> projectsToAdd = List.copyOf(newProjects);
        Set<ProjectIdentity> incomingIdentities = new HashSet<>();

        for (GeoProject project : projectsToAdd) {
            Objects.requireNonNull(project, "project");

            ProjectIdentity identity =
                    new ProjectIdentity(project.projectCode());

            if (projectIdentities.contains(identity)
                    || !incomingIdentities.add(identity)) {
                throw new DuplicateProjectException(
                        project.projectCode()
                );
            }
        }

        projectIdentities.addAll(incomingIdentities);
        projects.addAll(projectsToAdd);
    }

    public synchronized List<GeoProject> findAll() {
        return List.copyOf(projects);
    }

    public synchronized List<GeoProject> findRecent(int limit) {
        if (limit <= 0 || projects.isEmpty()) {
            return List.of();
        }

        int fromIndex = Math.max(0, projects.size() - limit);

        return List.copyOf(
                projects.subList(fromIndex, projects.size())
        );
    }

    public synchronized Optional<GeoProject> findByIntakePosition(
            int intakePosition
    ) {
        if (intakePosition < 1 || intakePosition > projects.size()) {
            return Optional.empty();
        }

        return Optional.of(projects.get(intakePosition - 1));
    }

    public synchronized boolean containsProjectCode(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        return projectIdentities.contains(
                new ProjectIdentity(projectCode)
        );
    }

    public synchronized Optional<GeoProject> findByProjectCode(
            String projectCode
    ) {
        Objects.requireNonNull(projectCode, "projectCode");

        return projects.stream()
                .filter(project -> project.projectCode().equals(projectCode))
                .findFirst();
    }

    public synchronized int size() {
        return projects.size();
    }
}
