package com.atlasgrid.geoops.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class ProjectCrsCatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesSortedUniqueCoordinateReferenceSystems() throws Exception {
        createProject("TX-AUS-033", "EPSG:4326");
        createProject("TX-DAL-033", "EPSG:3857");
        createProject("TX-HOU-033", "EPSG:4326");
        createProject("TX-SAT-033", "EPSG:26914");

        mockMvc.perform(get("/api/projects/crs-catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("EPSG:26914"))
                .andExpect(jsonPath("$[1]").value("EPSG:3857"))
                .andExpect(jsonPath("$[2]").value("EPSG:4326"))
                .andExpect(jsonPath("$.length()").value(3));
    }

    private void createProject(String projectCode, String crs)
            throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "TreeSet CRS Project",
                                  "coordinateReferenceSystem": "%s"
                                }
                                """.formatted(projectCode, crs)))
                .andExpect(status().isCreated());
    }
}
