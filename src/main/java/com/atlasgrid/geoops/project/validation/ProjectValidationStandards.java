package com.atlasgrid.geoops.project.validation;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Stable validation standards and stateless helpers shared by GeoOps
 * project-intake rules.
 *
 * <p>The class is final because it is a utility/policy holder and is not
 * designed as a base class. Its methods are static because they do not depend
 * on per-object state or injected services.</p>
 */
public final class ProjectValidationStandards {

    public static final String PROJECT_CODE_EXAMPLE = "TX-AUS-001";
    public static final String CRS_EXAMPLE = "EPSG:4326";

    private static final Pattern PROJECT_CODE_PATTERN =
            Pattern.compile("[A-Z]{2,5}-[A-Z]{2,5}-\\d{3}");

    private static final Pattern EPSG_CODE_PATTERN =
            Pattern.compile("EPSG:\\d+");

    private ProjectValidationStandards() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidProjectCode(String projectCode) {
        return PROJECT_CODE_PATTERN.matcher(projectCode).matches();
    }

    public static String normalizeCrsIdentifier(String coordinateReferenceSystem) {
        return coordinateReferenceSystem.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isValidCrsIdentifier(String coordinateReferenceSystem) {
        return EPSG_CODE_PATTERN
                .matcher(normalizeCrsIdentifier(coordinateReferenceSystem))
                .matches();
    }
}
