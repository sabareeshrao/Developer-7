package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Encapsulates the starter coordinate-reference-system rule.
 *
 * <p>Set 5 validates only the identifier shape. It does not yet query an EPSG
 * registry or perform coordinate transformations.</p>
 */
@Component
public class CoordinateReferenceSystemValidationRule implements ProjectValidationRule {

    private static final Pattern EPSG_CODE = Pattern.compile("EPSG:\\d+");

    @Override
    public String code() {
        return "CRS_FORMAT";
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        String normalized = request.coordinateReferenceSystem()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (EPSG_CODE.matcher(normalized).matches()) {
            return List.of();
        }

        return List.of(new ValidationIssue(
                code(),
                "Coordinate reference system must use an EPSG identifier such as EPSG:4326"
        ));
    }
}
