package com.atlasgrid.geoops.project.snapshot;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.application.ProjectService;
import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectSnapshotServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectSnapshotService snapshotService;

    @Test
    void serializesAndDeserializesTrustedCatalogSnapshot() {
        projectService.create(new CreateProjectRequest(
                "TX-AUS-521",
                "Snapshot Survey",
                " epsg:4326 "
        ));

        byte[] serialized = snapshotService.serializeCurrentCatalog();

        assertThat(serialized)
                .startsWith((byte) 0xAC, (byte) 0xED);

        ProjectSnapshotDocument snapshot =
                snapshotService.deserializeSnapshot(serialized);

        assertThat(snapshot.schemaVersion()).isEqualTo(1);
        assertThat(snapshot.projectCount()).isEqualTo(1);
        assertThat(snapshot.projects())
                .singleElement()
                .satisfies(entry -> {
                    assertThat(entry.projectCode())
                            .isEqualTo("TX-AUS-521");
                    assertThat(entry.name())
                            .isEqualTo("Snapshot Survey");
                    assertThat(entry.coordinateReferenceSystem())
                            .isEqualTo("EPSG:4326");
                });
    }

    @Test
    void keepsCoreDomainModelIndependentOfNativeSerialization() {
        assertThat(
                Serializable.class.isAssignableFrom(GeoProject.class)
        ).isFalse();

        assertThat(
                Serializable.class.isAssignableFrom(
                        ProjectSnapshotDocument.class
                )
        ).isTrue();
    }
}
