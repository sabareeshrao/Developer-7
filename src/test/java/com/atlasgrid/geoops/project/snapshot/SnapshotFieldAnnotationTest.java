package com.atlasgrid.geoops.project.snapshot;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SnapshotFieldAnnotationTest {

    private final SnapshotReflectionInspector inspector =
            new SnapshotReflectionInspector();

    @Test
    void customAnnotationIsAvailableAtRuntimeOnRecordComponents() {
        Retention retention =
                SnapshotField.class.getAnnotation(Retention.class);
        Target target =
                SnapshotField.class.getAnnotation(Target.class);

        assertThat(retention.value())
                .isEqualTo(RetentionPolicy.RUNTIME);
        assertThat(target.value())
                .containsExactly(
                        java.lang.annotation.ElementType.RECORD_COMPONENT
                );
    }

    @Test
    void reflectionInspectorReadsCustomSnapshotMetadata() {
        List<SnapshotFieldMetadata> fields =
                inspector.inspectSnapshotFields();

        assertThat(fields)
                .extracting(SnapshotFieldMetadata::name)
                .containsExactly(
                        "projectId",
                        "projectCode",
                        "name",
                        "coordinateReferenceSystem",
                        "createdAtEpochMilli"
                );

        assertThat(fields)
                .allSatisfy(field -> {
                    assertThat(field.description()).isNotBlank();
                    assertThat(field.required()).isTrue();
                });
    }
}
