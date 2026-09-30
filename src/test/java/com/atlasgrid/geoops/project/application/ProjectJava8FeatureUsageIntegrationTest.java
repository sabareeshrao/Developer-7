package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Set 45 evidence that the Java 8 features named in the experience answer
 * already work together in the real GeoOps project flow.
 *
 * <p>Project creation executes the lambda-backed validation rule, the domain
 * model records creation time with java.time.Instant, project lookup returns
 * Optional, and delivery selection uses the established Stream pipeline.</p>
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectJava8FeatureUsageIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDeliverySelectionService projectDeliverySelectionService;

    @Test
    void java8FeaturesWorkTogetherInProjectIntakeAndLookupFlow() {
        Instant beforeCreate = Instant.now();

        GeoProject project = projectService.create(
                new CreateProjectRequest(
                        "TX-AUS-045",
                        "Java 8 Feature Project",
                        "EPSG:4326"
                )
        );

        Instant afterCreate = Instant.now();

        assertThat(project.createdAt())
                .isBetween(beforeCreate, afterCreate);

        Optional<GeoProject> found =
                projectService.findByProjectCode("TX-AUS-045");

        assertThat(found)
                .isPresent()
                .contains(project);

        ProjectDeliverySelection selection =
                projectDeliverySelectionService.select(
                        projectService.findAll(),
                        "EPSG:4326"
                );

        assertThat(selection.projectCodes())
                .containsExactly("TX-AUS-045");
    }
}
