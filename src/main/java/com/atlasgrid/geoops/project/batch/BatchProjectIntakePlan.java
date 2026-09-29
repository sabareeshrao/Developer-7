package com.atlasgrid.geoops.project.batch;

import java.util.List;

/**
 * Read-only result of reconciling one submitted project batch.
 */
public record BatchProjectIntakePlan(
        int submittedCount,
        int uniqueProjectCount,
        int duplicateSubmissionCount,
        List<BatchProjectIntakeEntry> entries
) {
    public BatchProjectIntakePlan {
        entries = List.copyOf(entries);
    }
}
