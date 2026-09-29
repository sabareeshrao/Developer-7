package com.atlasgrid.geoops.review;

import java.util.Objects;

/**
 * One project waiting for GeoOps quality review.
 */
public record ProjectReviewTask(String projectCode) {

    public ProjectReviewTask {
        Objects.requireNonNull(projectCode, "projectCode");

        if (projectCode.isBlank()) {
            throw new IllegalArgumentException("projectCode must not be blank");
        }
    }
}
