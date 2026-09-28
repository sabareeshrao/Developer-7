package com.atlasgrid.geoops.project.api;

import jakarta.validation.constraints.NotBlank;

/**
 * API input object kept separate from the domain object.
 */
public record CreateProjectRequest(
        @NotBlank String projectCode,
        @NotBlank String name,
        @NotBlank String coordinateReferenceSystem
) {
}
