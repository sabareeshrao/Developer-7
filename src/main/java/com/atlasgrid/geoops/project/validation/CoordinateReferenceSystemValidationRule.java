package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Encapsulates the starter coordinate-reference-system rule.
 *
 * <p>Set 5 validates only the identifier shape. It does not yet query an EPSG
 * registry or perform coordinate transformations.</p>
 *
 * <p>This concrete rule is final because GeoOps extends validation by adding a
 * new ProjectValidationRule implementation rather than subclassing an existing
 * focused rule.</p>
 */
@Component
public final class CoordinateReferenceSystemValidationRule implements ProjectValidationRule {

    @Override
    public String code() {
        return "CRS_FORMAT";
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        if (ProjectValidationStandards
                .isValidCrsIdentifier(request.coordinateReferenceSystem())) {
            return List.of();
        }

        return List.of(new ValidationIssue(
                code(),
                "Coordinate reference system must use an EPSG identifier such as "
                        + ProjectValidationStandards.CRS_EXAMPLE
        ));
    }
}
