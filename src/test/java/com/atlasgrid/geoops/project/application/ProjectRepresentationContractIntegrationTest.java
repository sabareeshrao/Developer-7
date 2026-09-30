package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.snapshot.ProjectSnapshotDocument;
import com.atlasgrid.geoops.project.snapshot.ProjectSnapshotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectRepresentationContractIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectSnapshotService snapshotService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sameProjectCanCrossJsonCsvAndTrustedSnapshotBoundaries()
            throws Exception {
        projectService.create(new CreateProjectRequest(
                "TX-AUS-531",
                "Representation Survey",
                "EPSG:4326"
        ));

        mockMvc.perform(get("/api/projects")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode")
                        .value("TX-AUS-531"));

        mockMvc.perform(get("/api/projects/exports/csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "TX-AUS-531,Representation Survey,EPSG:4326"
                        )
                ));

        ProjectSnapshotDocument snapshot =
                snapshotService.deserializeSnapshot(
                        snapshotService.serializeCurrentCatalog()
                );

        assertThat(snapshot.projects())
                .singleElement()
                .satisfies(entry -> {
                    assertThat(entry.projectCode())
                            .isEqualTo("TX-AUS-531");
                    assertThat(entry.coordinateReferenceSystem())
                            .isEqualTo("EPSG:4326");
                });
    }
}
