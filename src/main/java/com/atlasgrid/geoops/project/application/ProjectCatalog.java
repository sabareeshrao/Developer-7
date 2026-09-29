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
 * and HashSet for the current behavior. It never exposes its mutable
 * collections directly; callers receive immutable snapshots.</p>
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

    public Optional<GeoProject> findByProjectCode(String projectCode) {
        return projects.stream()
                .filter(project -> project.projectCode().equals(projectCode))
                .findFirst();
    }

    public int size() {
        return projects.size();
    }
}
