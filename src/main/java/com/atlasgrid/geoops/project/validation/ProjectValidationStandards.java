package com.atlasgrid.geoops.project.validation;

import java.util.regex.Pattern;

/**
 * Stable validation standards shared by the GeoOps project-intake rules.
 *
 * <p>The class is final because it is a constants/policy holder and is not
 * designed as a base class. The fields are static final so the references
 * cannot be reassigned after class initialization.</p>
 */
public final class ProjectValidationStandards {

    public static final String PROJECT_CODE_EXAMPLE = "TX-AUS-001";
    public static final String CRS_EXAMPLE = "EPSG:4326";

    static final Pattern PROJECT_CODE_PATTERN =
            Pattern.compile("[A-Z]{2,5}-[A-Z]{2,5}-\\d{3}");

    static final Pattern EPSG_CODE_PATTERN =
            Pattern.compile("EPSG:\\d+");

    private ProjectValidationStandards() {
        throw new IllegalStateException("Utility class");
    }
}
