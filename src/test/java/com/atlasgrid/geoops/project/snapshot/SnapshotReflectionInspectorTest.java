package com.atlasgrid.geoops.project.snapshot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SnapshotReflectionInspectorTest {

    private final SnapshotReflectionInspector inspector =
            new SnapshotReflectionInspector();

    @Test
    void inspectsRecordComponentsWithoutBreakingEncapsulation() {
        SnapshotTypeReport report =
                inspector.inspectSnapshotEntry();

        assertThat(report.className())
                .isEqualTo(ProjectSnapshotEntry.class.getName());
        assertThat(report.recordType()).isTrue();
        assertThat(report.recordComponents()).containsExactly(
                "projectId:String",
                "projectCode:String",
                "name:String",
                "coordinateReferenceSystem:String",
                "createdAtEpochMilli:long"
        );
    }

    @Test
    void inspectsSnapshotDocumentInstanceFields() {
        SnapshotTypeReport report =
                inspector.inspectSnapshotDocument();

        assertThat(report.recordType()).isFalse();
        assertThat(report.declaredInstanceFields()).containsExactly(
                "capturedAtEpochMilli",
                "projects",
                "schemaVersion"
        );
    }
}
