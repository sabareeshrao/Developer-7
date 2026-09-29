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
class RecentProjectsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsMostRecentProjectsWithoutChangingCatalogOrder() throws Exception {
        createProject("TX-AUS-030");
        createProject("TX-DAL-030");
        createProject("TX-HOU-030");
        createProject("TX-SAT-030");

        mockMvc.perform(get("/api/projects/recent").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-HOU-030"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-SAT-030"));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-AUS-030"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-DAL-030"))
                .andExpect(jsonPath("$[2].projectCode").value("TX-HOU-030"))
                .andExpect(jsonPath("$[3].projectCode").value("TX-SAT-030"));
    }

    @Test
    void nonPositiveRecentLimitReturnsEmptyList() throws Exception {
        createProject("TX-AUS-030");

        mockMvc.perform(get("/api/projects/recent").param("limit", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private void createProject(String projectCode) throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "Recent Intake Project",
                                  "coordinateReferenceSystem": "EPSG:4326"
                                }
                                """.formatted(projectCode)))
                .andExpect(status().isCreated());
    }
}
