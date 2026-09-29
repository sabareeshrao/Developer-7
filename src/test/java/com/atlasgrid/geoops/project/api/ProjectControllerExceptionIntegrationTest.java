package com.atlasgrid.geoops.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProjectControllerExceptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void domainValidationExceptionReturnsStructuredBadRequest() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "bad-code",
                                  "name": "Invalid Survey Intake",
                                  "coordinateReferenceSystem": "WGS84"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Project request failed domain validation"))
                .andExpect(jsonPath("$.details", hasItems(
                        "PROJECT_CODE_FORMAT: Project code must follow a pattern such as TX-AUS-001",
                        "CRS_FORMAT: Coordinate reference system must use an EPSG identifier such as EPSG:4326"
                )));
    }

    @Test
    void duplicateProjectExceptionReturnsStructuredConflict() throws Exception {
        String request = """
                {
                  "projectCode": "TX-AUS-777",
                  "name": "Duplicate Check",
                  "coordinateReferenceSystem": "EPSG:4326"
                }
                """;

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Project already exists for projectCode=TX-AUS-777"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    void beanValidationAlsoUsesCentralErrorContract() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "",
                                  "name": "Blank Code",
                                  "coordinateReferenceSystem": "EPSG:4326"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request failed API validation"))
                .andExpect(jsonPath("$.details").isArray());
    }
}
