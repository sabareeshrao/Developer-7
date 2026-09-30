package com.atlasgrid.geoops.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectDataTransferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void importsCsvAndExportsCurrentCatalog() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "projects.csv",
                "text/csv",
                """
                projectCode,name,coordinateReferenceSystem
                TX-AUS-052,"Austin, Transfer Survey",epsg:4326
                TX-DAL-052,Dallas Transfer Survey,EPSG:3857
                """.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/projects/imports/csv")
                                .file(file)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.importedCount").value(2))
                .andExpect(jsonPath("$.projectCodes[0]")
                        .value("TX-AUS-052"))
                .andExpect(jsonPath("$.projectCodes[1]")
                        .value("TX-DAL-052"));

        mockMvc.perform(get("/api/projects/exports/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(header().string(
                        "Content-Disposition",
                        containsString("geoops-projects.csv")
                ))
                .andExpect(content().string(
                        containsString(
                                "TX-AUS-052,"
                                        + '"'
                                        + "Austin, Transfer Survey"
                                        + '"'
                                        + ",EPSG:4326"
                        )
                ))
                .andExpect(content().string(
                        containsString(
                                "TX-DAL-052,Dallas Transfer Survey,EPSG:3857"
                        )
                ));
    }
}
