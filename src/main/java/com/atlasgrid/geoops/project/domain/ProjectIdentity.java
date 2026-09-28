package com.atlasgrid.geoops.project.domain;

import java.util.Objects;

/**
 * Business identity for one GeoOps project.
 *
 * <p>Two different ProjectIdentity objects represent the same logical project
 * when they contain the same project code. equals() and hashCode() are
 * overridden together so this object behaves correctly in HashSet/HashMap.</p>
 */
public final class ProjectIdentity {

    private final String projectCode;

    public ProjectIdentity(String projectCode) {
        this.projectCode = Objects.requireNonNull(projectCode, "projectCode");
    }

    public String projectCode() {
        return projectCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProjectIdentity that)) {
            return false;
        }
        return projectCode.equals(that.projectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectCode);
    }

    @Override
    public String toString() {
        return projectCode;
    }
}
