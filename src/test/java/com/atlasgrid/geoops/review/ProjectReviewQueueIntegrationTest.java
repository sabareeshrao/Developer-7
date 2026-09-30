package com.atlasgrid.geoops.review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectReviewQueueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void claimedProjectCanBeRetriedOnlyAfterClaim() throws Exception {
        createProject("TX-AUS-034");
        createProject("TX-DAL-034");

        mockMvc.perform(post("/api/review-queue/TX-AUS-034/retry"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/review-queue/claim-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCode").value("TX-AUS-034"));

        mockMvc.perform(post("/api/review-queue/TX-AUS-034/retry"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/review-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-AUS-034"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-DAL-034"));
    }

    @Test
    void completedClaimCannotBeRetriedAgain() throws Exception {
        createProject("TX-AUS-034");

        mockMvc.perform(post("/api/review-queue/claim-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCode").value("TX-AUS-034"));

        mockMvc.perform(post("/api/review-queue/TX-AUS-034/complete"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/review-queue/TX-AUS-034/retry"))
                .andExpect(status().isNotFound());
    }

    @Test
    void queuedProjectCanBeExpeditedToFront() throws Exception {
        createProject("TX-AUS-031");
        createProject("TX-DAL-031");
        createProject("TX-HOU-031");

        mockMvc.perform(post("/api/review-queue/TX-HOU-031/expedite"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/review-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-HOU-031"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-AUS-031"))
                .andExpect(jsonPath("$[2].projectCode").value("TX-DAL-031"));
    }

    @Test
    void queuedProjectCanBeDeferredToTail() throws Exception {
        createProject("TX-AUS-032");
        createProject("TX-DAL-032");
        createProject("TX-HOU-032");

        mockMvc.perform(post("/api/review-queue/TX-AUS-032/defer"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/review-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-DAL-032"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-HOU-032"))
                .andExpect(jsonPath("$[2].projectCode").value("TX-AUS-032"));
    }

    @Test
    void queuedProjectCanBeCancelledWithoutBreakingOrder() throws Exception {
        createProject("TX-HOU-024");
        createProject("TX-SAT-024");
        createProject("TX-ELP-024");

        mockMvc.perform(delete("/api/review-queue/TX-SAT-024"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/review-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-HOU-024"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-ELP-024"));
    }

    private void createProject(String projectCode) throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "Review Workflow Project",
                                  "coordinateReferenceSystem": "EPSG:4326"
                                }
                                """.formatted(projectCode)))
                .andExpect(status().isCreated());
    }
}
