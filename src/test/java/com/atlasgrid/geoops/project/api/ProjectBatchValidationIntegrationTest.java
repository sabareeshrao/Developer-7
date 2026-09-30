package com.atlasgrid.geoops.project.api;

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
class ProjectBatchValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validatesBatchWithoutMutatingProjectCatalog()
            throws Exception {
        mockMvc.perform(post("/api/projects/validation/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {
                                    "projectCode": "TX-AUS-630",
                                    "name": "Valid Batch Request",
                                    "coordinateReferenceSystem": "EPSG:4326"
                                  },
                                  {
                                    "projectCode": "bad-code",
                                    "name": "Invalid Batch Request",
                                    "coordinateReferenceSystem": "EPSG:4326"
                                  }
                                ]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode")
                        .value("TX-AUS-630"))
                .andExpect(jsonPath("$[0].valid")
                        .value(true))
                .andExpect(jsonPath("$[1].projectCode")
                        .value("bad-code"))
                .andExpect(jsonPath("$[1].valid")
                        .value(false));
    }
}
