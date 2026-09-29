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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectSortingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesArrayAndCollectionSortedViewsWithoutChangingCatalogOrder()
            throws Exception {
        createProject("TX-HOU-029", "EPSG:4326");
        createProject("TX-AUS-029", "EPSG:3857");
        createProject("TX-DAL-029", "EPSG:4326");

        mockMvc.perform(get("/api/projects/sorting-preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCodesAlphabetically[0]")
                        .value("TX-AUS-029"))
                .andExpect(jsonPath("$.projectCodesAlphabetically[1]")
                        .value("TX-DAL-029"))
                .andExpect(jsonPath("$.projectCodesAlphabetically[2]")
                        .value("TX-HOU-029"))
                .andExpect(jsonPath("$.projectsByCrsThenCode[0].projectCode")
                        .value("TX-AUS-029"))
                .andExpect(jsonPath("$.projectsByCrsThenCode[1].projectCode")
                        .value("TX-DAL-029"))
                .andExpect(jsonPath("$.projectsByCrsThenCode[2].projectCode")
                        .value("TX-HOU-029"));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-HOU-029"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-AUS-029"))
                .andExpect(jsonPath("$[2].projectCode").value("TX-DAL-029"));
    }

    private void createProject(String projectCode, String crs)
            throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "Sorting Project",
                                  "coordinateReferenceSystem": "%s"
                                }
                                """.formatted(projectCode, crs)))
                .andExpect(status().isCreated());
    }
}
