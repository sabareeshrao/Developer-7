package com.atlasgrid.geoops.project.snapshot;

/**
 * Reflection view of one custom-annotated snapshot record component.
 */
public record SnapshotFieldMetadata(
        String name,
        String type,
        String description,
        boolean required
) {
}
