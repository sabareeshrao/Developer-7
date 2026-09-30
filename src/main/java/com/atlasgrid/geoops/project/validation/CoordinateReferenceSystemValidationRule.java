package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Encapsulates the starter coordinate-reference-system rule.
 *
 * <p>The rule validates identifier shape only. Canonicalization is performed
 * by ProjectService before accepted projects are stored.</p>
 */
@Component
@Order(20)
public final class CoordinateReferenceSystemValidationRule
        implements ProjectValidationRule {

    @Override
    public String code() {
        return "CRS_FORMAT";
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        String crs = request.coordinateReferenceSystem();

        if (crs == null || crs.isBlank()) {
            return List.of();
        }

        if (ProjectValidationStandards.isValidCrsIdentifier(crs)) {
            return List.of();
        }

        return List.of(new ValidationIssue(
                code(),
                "Coordinate reference system must use an EPSG identifier such as "
                        + ProjectValidationStandards.CRS_EXAMPLE
        ));
    }
}
