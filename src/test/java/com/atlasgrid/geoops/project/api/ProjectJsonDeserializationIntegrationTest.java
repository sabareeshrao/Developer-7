package com.atlasgrid.geoops.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectJsonDeserializationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void acceptsKnownSnakeCaseAliasesAndUsesNormalProjectWorkflow()
            throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "project_code": "TX-AUS-551",
                                  "name": "Legacy JSON Survey",
                                  "coordinate_reference_system": " epsg:4326 "
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectCode").value("TX-AUS-551"))
                .andExpect(jsonPath("$.name").value("Legacy JSON Survey"))
                .andExpect(jsonPath("$.coordinateReferenceSystem")
                        .value("EPSG:4326"));
    }

    @Test
    void rejectsUnknownJsonFieldsInsteadOfSilentlyIgnoringContractDrift()
            throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "TX-AUS-552",
                                  "name": "Strict JSON Survey",
                                  "coordinateReferenceSystem": "EPSG:4326",
                                  "coordinateReferenseSystem": "typo"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUEST_MALFORMED"))
                .andExpect(jsonPath("$.message")
                        .value("Request body is malformed or unreadable"));
    }
}
