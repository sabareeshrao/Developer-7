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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Proves that the three collection choices highlighted by Set 27 participate in
 * one coherent project-intake workflow:
 *
 * <ul>
 *     <li>ArrayList-backed project storage preserves intake order.</li>
 *     <li>HashSet-backed project identity prevents duplicates and answers
 *         membership checks.</li>
 *     <li>LinkedList-backed review worklist preserves FIFO review order.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectCollectionStrategyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listLinkedListAndHashSetWorkTogetherAcrossProjectIntake() throws Exception {
        createProject("TX-AUS-027");
        createProject("TX-DAL-027");

        // List / ArrayList: intake order is preserved.
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-AUS-027"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-DAL-027"));

        // Set / HashSet: membership lookup uses ProjectIdentity.
        mockMvc.perform(get("/api/projects/exists/TX-AUS-027"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/api/projects/exists/TX-HOU-027"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        // Duplicate logical identity is rejected before a second review task is added.
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson("TX-AUS-027")))
                .andExpect(status().isConflict());

        // Deque / LinkedList: review work remains FIFO.
        mockMvc.perform(get("/api/review-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectCode").value("TX-AUS-027"))
                .andExpect(jsonPath("$[1].projectCode").value("TX-DAL-027"))
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(post("/api/review-queue/claim-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCode").value("TX-AUS-027"));
    }

    private void createProject(String projectCode) throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson(projectCode)))
                .andExpect(status().isCreated());
    }

    private String projectJson(String projectCode) {
        return """
                {
                  "projectCode": "%s",
                  "name": "Collection Strategy Project",
                  "coordinateReferenceSystem": "EPSG:4326"
                }
                """.formatted(projectCode);
    }
}
