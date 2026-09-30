package com.atlasgrid.geoops.project.snapshot;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Uses Java Reflection for snapshot-schema diagnostics.
 *
 * <p>The inspector reads type metadata only. It does not bypass encapsulation,
 * mutate private fields, or invoke arbitrary methods.</p>
 */
public class SnapshotReflectionInspector {

    public SnapshotTypeReport inspect(Class<?> type) {
        Objects.requireNonNull(type, "type");

        List<String> components = type.isRecord()
                ? Arrays.stream(type.getRecordComponents())
                        .map(this::describe)
                        .toList()
                : List.of();

        List<String> fields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getName)
                .sorted()
                .toList();

        return new SnapshotTypeReport(
                type.getName(),
                type.isRecord(),
                components,
                fields
        );
    }

    public SnapshotTypeReport inspectSnapshotEntry() {
        return inspect(ProjectSnapshotEntry.class);
    }

    public SnapshotTypeReport inspectSnapshotDocument() {
        return inspect(ProjectSnapshotDocument.class);
    }

    public List<SnapshotFieldMetadata> inspectSnapshotFields() {
        return Arrays.stream(
                        ProjectSnapshotEntry.class.getRecordComponents()
                )
                .filter(component ->
                        component.isAnnotationPresent(
                                SnapshotField.class
                        )
                )
                .map(this::toMetadata)
                .toList();
    }

    private SnapshotFieldMetadata toMetadata(
            RecordComponent component
    ) {
        SnapshotField annotation =
                component.getAnnotation(SnapshotField.class);

        return new SnapshotFieldMetadata(
                component.getName(),
                component.getType().getSimpleName(),
                annotation.description(),
                annotation.required()
        );
    }

    private String describe(RecordComponent component) {
        return component.getName()
                + ":"
                + component.getType().getSimpleName();
    }
}
