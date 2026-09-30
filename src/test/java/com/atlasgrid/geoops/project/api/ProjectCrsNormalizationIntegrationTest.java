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
class ProjectCrsNormalizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void storesAndQueriesCanonicalCrs() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "TX-AUS-503",
                                  "name": "Normalization Project",
                                  "coordinateReferenceSystem": " epsg:4326 "
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.coordinateReferenceSystem")
                        .value("EPSG:4326"));

        mockMvc.perform(get("/api/projects/delivery-selection")
                        .param("crs", "epsg:4326"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coordinateReferenceSystem")
                        .value("EPSG:4326"))
                .andExpect(jsonPath("$.projectCodes[0]")
                        .value("TX-AUS-503"));
    }
}
