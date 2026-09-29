package com.atlasgrid.geoops.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerSnapshotIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void snapshotEndpointSerializesImmutableSnapshotAsJson() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "TX-AUS-901",
                                  "name": "Snapshot Serialization Check",
                                  "coordinateReferenceSystem": "EPSG:4326"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/projects/snapshot"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.capturedAt").isNotEmpty())
                .andExpect(jsonPath("$.projectCount").value(1))
                .andExpect(jsonPath("$.projects[0].projectCode").value("TX-AUS-901"))
                .andExpect(jsonPath("$.projects[0].coordinateReferenceSystem").value("EPSG:4326"));
    }
}
