package com.atlasgrid.geoops.project.batch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BatchProjectIntakeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void reconcilesDuplicateProjectCodesInFirstSeenOrder() throws Exception {
        mockMvc.perform(post("/api/projects/batch-plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {
                                    "projectCode": "TX-AUS-025",
                                    "name": "Austin",
                                    "coordinateReferenceSystem": "EPSG:4326"
                                  },
                                  {
                                    "projectCode": "TX-DAL-025",
                                    "name": "Dallas",
                                    "coordinateReferenceSystem": "EPSG:3857"
                                  },
                                  {
                                    "projectCode": "TX-AUS-025",
                                    "name": "Austin duplicate",
                                    "coordinateReferenceSystem": "EPSG:3857"
                                  },
                                  {
                                    "projectCode": "TX-HOU-025",
                                    "name": "Houston",
                                    "coordinateReferenceSystem": "EPSG:4326"
                                  }
                                ]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submittedCount").value(4))
                .andExpect(jsonPath("$.uniqueProjectCount").value(3))
                .andExpect(jsonPath("$.duplicateSubmissionCount").value(1))
                .andExpect(jsonPath("$.entries[0].projectCode")
                        .value("TX-AUS-025"))
                .andExpect(jsonPath("$.entries[0].occurrenceCount")
                        .value(2))
                .andExpect(jsonPath("$.entries[1].projectCode")
                        .value("TX-DAL-025"))
                .andExpect(jsonPath("$.entries[2].projectCode")
                        .value("TX-HOU-025"));
    }
}
