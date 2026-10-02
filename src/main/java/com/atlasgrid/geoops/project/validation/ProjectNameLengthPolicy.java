package com.atlasgrid.geoops.project.validation;

/**
 * Owns the project-name length limit; independent of Spring and validation
 * report formatting.
 */
public final class ProjectNameLengthPolicy {

    public static final int MAX_NAME_LENGTH = 120;

    private ProjectNameLengthPolicy() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean exceedsLimit(String name) {
        return name != null && name.length() > MAX_NAME_LENGTH;
    }
}
