package com.atlasgrid.geoops.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParallelValidationFeatureIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validatesLargeMixedBatchInOrderWithoutPublishingProjects()
            throws Exception {
        String payload = IntStream.range(0, 100)
                .mapToObj(index -> """
                        {
                          "projectCode": "%s",
                          "name": "Batch Feature %d",
                          "coordinateReferenceSystem": "EPSG:4326"
                        }
                        """.formatted(
                        index % 10 == 0
                                ? "bad-code-" + index
                                : "TX-BATCH-%03d".formatted(index),
                        index
                ))
                .collect(Collectors.joining(",", "[", "]"));

        mockMvc.perform(post("/api/projects/validation/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100))
                .andExpect(jsonPath("$[0].projectCode").value("bad-code-0"))
                .andExpect(jsonPath("$[0].valid").value(false))
                .andExpect(jsonPath("$[1].projectCode").value("TX-BATCH-001"))
                .andExpect(jsonPath("$[1].valid").value(true))
                .andExpect(jsonPath("$[99].projectCode").value("TX-BATCH-099"))
                .andExpect(jsonPath("$[99].valid").value(true));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
