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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProjectIntakePositionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsProjectByOneBasedIntakePosition() throws Exception {
        createProject("TX-AUS-023");
        createProject("TX-DAL-023");
        createProject("TX-HOU-023");

        mockMvc.perform(get("/api/projects/by-position/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCode").value("TX-DAL-023"));
    }

    @Test
    void returnsNotFoundForPositionOutsideCatalog() throws Exception {
        mockMvc.perform(get("/api/projects/by-position/99"))
                .andExpect(status().isNotFound());
    }

    private void createProject(String projectCode) throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "ArrayList Intake Project",
                                  "coordinateReferenceSystem": "EPSG:4326"
                                }
                                """.formatted(projectCode)))
                .andExpect(status().isCreated());
    }
}
