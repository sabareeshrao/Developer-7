package com.atlasgrid.geoops.project.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

/**
 * API input object kept separate from the domain object.
 *
 * <p>Known legacy snake_case aliases are accepted at the JSON boundary so
 * GeoOps can evolve an external request contract without weakening the Java
 * naming convention used by the application.</p>
 */
public record CreateProjectRequest(
        @JsonAlias("project_code")
        @NotBlank String projectCode,
        @NotBlank String name,
        @JsonAlias("coordinate_reference_system")
        @NotBlank String coordinateReferenceSystem
) {
}
