package com.atlasgrid.geoops.review;

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
class ProjectReviewQueueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void newlyCreatedProjectsEnterReviewQueueInFifoOrder() throws Exception {
        createProject("TX-AUS-024");
        createProject("TX-DAL-024");

        mockMvc.perform(get("/api/review-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-AUS-024"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-DAL-024"));

        mockMvc.perform(post("/api/review-queue/claim-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCode").value("TX-AUS-024"));

        mockMvc.perform(post("/api/review-queue/claim-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCode").value("TX-DAL-024"));
    }

    private void createProject(String projectCode) throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "LinkedList Review Project",
                                  "coordinateReferenceSystem": "EPSG:4326"
                                }
                                """.formatted(projectCode)))
                .andExpect(status().isCreated());
    }
}
