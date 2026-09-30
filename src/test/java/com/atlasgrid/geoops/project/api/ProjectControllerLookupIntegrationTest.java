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
class ProjectControllerLookupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void lookupUsesNormalHttpControlFlowForPresentAndMissingProjects() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "TX-AUS-616",
                                  "name": "Lookup Strategy Project",
                                  "coordinateReferenceSystem": "EPSG:4326"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/projects/by-code/TX-AUS-616"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCode").value("TX-AUS-616"));

        mockMvc.perform(get("/api/projects/by-code/TX-AUS-404"))
                .andExpect(status().isNotFound());
    }
}
