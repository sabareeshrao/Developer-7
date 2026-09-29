package com.atlasgrid.geoops.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProjectCollectionSummaryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void summarizesCurrentProjectCollectionThroughRestApi() throws Exception {
        createProject("TX-AUS-020", "EPSG:4326");
        createProject("TX-DAL-020", "EPSG:3857");
        createProject("TX-HOU-020", "EPSG:4326");

        mockMvc.perform(get("/api/projects/collection-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCount").value(3))
                .andExpect(jsonPath("$.distinctCoordinateReferenceSystemCount")
                        .value(2))
                .andExpect(jsonPath("$.projectCodesInIntakeOrder[0]")
                        .value("TX-AUS-020"))
                .andExpect(jsonPath("$.projectCodesInIntakeOrder[1]")
                        .value("TX-DAL-020"))
                .andExpect(jsonPath("$.projectCodesInIntakeOrder[2]")
                        .value("TX-HOU-020"))
                .andExpect(jsonPath(
                        "$.projectCountByCoordinateReferenceSystem['EPSG:4326']"
                ).value(2))
                .andExpect(jsonPath(
                        "$.projectCountByCoordinateReferenceSystem['EPSG:3857']"
                ).value(1));
    }

    private void createProject(String projectCode, String crs) throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "Collection Summary Project",
                                  "coordinateReferenceSystem": "%s"
                                }
                                """.formatted(projectCode, crs)))
                .andExpect(status().isCreated());
    }
}
