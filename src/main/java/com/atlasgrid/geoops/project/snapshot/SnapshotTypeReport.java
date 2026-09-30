package com.atlasgrid.geoops.project.snapshot;

import java.util.List;

/**
 * Read-only reflection metadata used to diagnose the internal snapshot schema.
 */
public record SnapshotTypeReport(
        String className,
        boolean recordType,
        List<String> recordComponents,
        List<String> declaredInstanceFields
) {
    public SnapshotTypeReport {
        recordComponents = List.copyOf(recordComponents);
        declaredInstanceFields = List.copyOf(declaredInstanceFields);
    }
}
