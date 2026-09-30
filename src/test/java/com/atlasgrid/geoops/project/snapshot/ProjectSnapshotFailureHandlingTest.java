package com.atlasgrid.geoops.project.snapshot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProjectSnapshotFailureHandlingTest {

    @Autowired
    private ProjectSnapshotService snapshotService;

    @Test
    void classifiesCorruptStream() {
        assertFailure(
                new byte[]{0x01, 0x02, 0x03, 0x04},
                ProjectSnapshotFailure.CORRUPT_STREAM
        );
    }

    @Test
    void rejectsUnsupportedSchemaVersion() throws IOException {
        ProjectSnapshotDocument document =
                new ProjectSnapshotDocument(
                        99,
                        0L,
                        new ProjectSnapshotEntry[0]
                );

        assertFailure(
                serialize(document),
                ProjectSnapshotFailure.UNSUPPORTED_SCHEMA
        );
    }

    @Test
    void rejectsTypesOutsideSnapshotAllowlist() throws IOException {
        assertFailure(
                serialize(new ArrayList<>()),
                ProjectSnapshotFailure.REJECTED_TYPE
        );
    }

    @Test
    void rejectsAllowedEntryWhenItIsNotTheSnapshotRoot()
            throws IOException {
        ProjectSnapshotEntry entry = new ProjectSnapshotEntry(
                "id",
                "TX-AUS-541",
                "Wrong Root",
                "EPSG:4326",
                0L
        );

        assertFailure(
                serialize(entry),
                ProjectSnapshotFailure.WRONG_ROOT_TYPE
        );
    }

    private void assertFailure(
            byte[] serialized,
            ProjectSnapshotFailure expected
    ) {
        assertThatThrownBy(
                () -> snapshotService.deserializeSnapshot(serialized)
        ).isInstanceOfSatisfying(
                ProjectSnapshotException.class,
                exception -> assertThat(exception.failure())
                        .isEqualTo(expected)
        );
    }

    private byte[] serialize(Object value) throws IOException {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
            output.flush();
            return bytes.toByteArray();
        }
    }
}
