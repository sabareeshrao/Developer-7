package com.atlasgrid.geoops.project.snapshot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProjectSnapshotMissingClassTest {

    private static final byte[] ORIGINAL_CLASS_NAME =
            "com.atlasgrid.geoops.project.snapshot.ProjectSnapshotDocument"
                    .getBytes(StandardCharsets.UTF_8);

    private static final byte[] MISSING_CLASS_NAME =
            "com.atlasgrid.geoops.project.snapshot.ProjectSnapshotDocumenz"
                    .getBytes(StandardCharsets.UTF_8);

    @Autowired
    private ProjectSnapshotService snapshotService;

    @Test
    void mapsRealObjectInputStreamClassNotFoundExceptionToMissingClass() {
        byte[] serialized = snapshotService.serializeCurrentCatalog();
        byte[] withMissingDescriptor =
                replaceFirst(
                        serialized,
                        ORIGINAL_CLASS_NAME,
                        MISSING_CLASS_NAME
                );

        assertThatThrownBy(
                () -> snapshotService.deserializeSnapshot(
                        withMissingDescriptor
                )
        ).isInstanceOfSatisfying(
                ProjectSnapshotException.class,
                exception -> {
                    assertThat(exception.failure())
                            .isEqualTo(
                                    ProjectSnapshotFailure.MISSING_CLASS
                            );
                    assertThat(exception.getCause())
                            .isInstanceOf(ClassNotFoundException.class);
                }
        );
    }

    private byte[] replaceFirst(
            byte[] source,
            byte[] target,
            byte[] replacement
    ) {
        if (target.length != replacement.length) {
            throw new IllegalArgumentException(
                    "Replacement must preserve serialized UTF length"
            );
        }

        byte[] copy = Arrays.copyOf(source, source.length);

        for (int index = 0;
             index <= copy.length - target.length;
             index++) {
            boolean matches = true;

            for (int offset = 0; offset < target.length; offset++) {
                if (copy[index + offset] != target[offset]) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                System.arraycopy(
                        replacement,
                        0,
                        copy,
                        index,
                        replacement.length
                );
                return copy;
            }
        }

        throw new AssertionError(
                "Serialized class descriptor was not found"
        );
    }
}
