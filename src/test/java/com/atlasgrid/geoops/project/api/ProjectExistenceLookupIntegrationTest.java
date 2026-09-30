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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectExistenceLookupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void checksProjectCodeMembershipThroughCatalogIdentitySet() throws Exception {
        createProject("TX-AUS-026");

        mockMvc.perform(get("/api/projects/exists/TX-AUS-026"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/api/projects/exists/TX-DAL-026"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    private void createProject(String projectCode) throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "Collection Inventory Project",
                                  "coordinateReferenceSystem": "EPSG:4326"
                                }
                                """.formatted(projectCode)))
                .andExpect(status().isCreated());
    }
}
