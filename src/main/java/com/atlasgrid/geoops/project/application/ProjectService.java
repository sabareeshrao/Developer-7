package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Application/service layer for the first GeoOps vertical slice.
 *
 * Storage is intentionally in-memory in Set 1. A later database-focused
 * anchor will replace it with a repository backed by PostgreSQL/PostGIS.
 */
@Slf4j
@Service
public class ProjectService {

    private final List<GeoProject> projects = new ArrayList<>();

    public List<GeoProject> findAll() {
        return List.copyOf(projects);
    }

    public GeoProject create(CreateProjectRequest request) {
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
