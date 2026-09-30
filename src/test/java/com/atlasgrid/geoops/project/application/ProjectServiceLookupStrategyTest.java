package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectServiceLookupStrategyTest {

    private final ProjectService service = ProjectServiceTestFactory.create();

    @Test
    void expectedMissingProjectUsesOptionalInsteadOfExceptionControlFlow() {
        assertThat(service.findByProjectCode("TX-AUS-404")).isEmpty();
    }

    @Test
    void existingProjectIsReturnedThroughOptional() {
        service.create(new CreateProjectRequest(
                "TX-AUS-616",
                "Lookup Strategy Project",
                "EPSG:4326"
        ));

        assertThat(service.findByProjectCode("TX-AUS-616"))
                .hasValueSatisfying(project ->
                        assertThat(project.projectCode())
                                .isEqualTo("TX-AUS-616")
                );
    }

    @Test
    void optionalCanTransformPresentProjectWithoutCallingGet() {
        service.create(new CreateProjectRequest(
                "TX-AUS-641",
                "Optional Mapping Project",
                "EPSG:4326"
        ));

        String projectCode = service.findByProjectCode("TX-AUS-641")
                .map(project -> project.projectCode())
                .orElse("MISSING");

        assertThat(projectCode).isEqualTo("TX-AUS-641");
    }

    @Test
    void orElseGetCreatesFallbackOnlyWhenLookupIsEmpty() {
        AtomicInteger fallbackCalls = new AtomicInteger();

        service.create(new CreateProjectRequest(
                "TX-DAL-641",
                "Optional Lazy Fallback Project",
                "EPSG:3857"
        ));

        String present = service.findByProjectCode("TX-DAL-641")
                .map(project -> project.projectCode())
                .orElseGet(() -> fallbackCode(fallbackCalls));

        assertThat(present).isEqualTo("TX-DAL-641");
        assertThat(fallbackCalls).hasValue(0);

        String missing = service.findByProjectCode("TX-HOU-404")
                .map(project -> project.projectCode())
                .orElseGet(() -> fallbackCode(fallbackCalls));

        assertThat(missing).isEqualTo("MISSING");
        assertThat(fallbackCalls).hasValue(1);
    }

    private String fallbackCode(AtomicInteger fallbackCalls) {
        fallbackCalls.incrementAndGet();
        return "MISSING";
    }
}
