package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.validation.ValidationIntakeSwitch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectBatchValidationSwitchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ValidationIntakeSwitch intakeSwitch;

    @Test
    void closedSwitchRefusesNewWorkThenResumes() throws Exception {
        String body = """
                [{
                  "projectCode": "TX-AUS-790",
                  "name": "Switch Test",
                  "coordinateReferenceSystem": "EPSG:4326"
                }]
                """;
        intakeSwitch.pause();
        mockMvc.perform(post("/api/projects/validation/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isServiceUnavailable());

        intakeSwitch.resume();
        mockMvc.perform(post("/api/projects/validation/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }
}
