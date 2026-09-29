package com.atlasgrid.geoops.project.batch;

/**
 * One unique project entry in a reconciled batch-intake plan.
 */
public record BatchProjectIntakeEntry(
        String projectCode,
        String name,
        String coordinateReferenceSystem,
        int occurrenceCount
) {
}
