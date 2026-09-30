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
class ProjectDeliverySelectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesStreamBasedDeliverySelectionWithoutChangingCatalogOrder()
            throws Exception {
        createProject("TX-HOU-040", "EPSG:4326");
        createProject("TX-AUS-040", "EPSG:3857");
        createProject("TX-DAL-040", "EPSG:4326");

        mockMvc.perform(get("/api/projects/delivery-selection")
                        .param("crs", "EPSG:4326"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coordinateReferenceSystem")
                        .value("EPSG:4326"))
                .andExpect(jsonPath("$.projectCount").value(2))
                .andExpect(jsonPath("$.projectCodes[0]")
                        .value("TX-DAL-040"))
                .andExpect(jsonPath("$.projectCodes[1]")
                        .value("TX-HOU-040"));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode")
                        .value("TX-HOU-040"))
                .andExpect(jsonPath("$[1].projectCode")
                        .value("TX-AUS-040"))
                .andExpect(jsonPath("$[2].projectCode")
                        .value("TX-DAL-040"));
    }

    private void createProject(String projectCode, String crs)
            throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectCode": "%s",
                                  "name": "Stream Delivery Project",
                                  "coordinateReferenceSystem": "%s"
                                }
                                """.formatted(projectCode, crs)))
                .andExpect(status().isCreated());
    }
}
