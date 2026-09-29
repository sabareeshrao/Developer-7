package com.atlasgrid.geoops.project.batch;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BatchProjectIntakePlannerTest {

    private final BatchProjectIntakePlanner planner =
            new BatchProjectIntakePlanner();

    @Test
    void deduplicatesByProjectIdentityWhilePreservingFirstSeenOrder() {
        BatchProjectIntakePlan plan = planner.plan(List.of(
                request("TX-AUS-025", "Austin A", "EPSG:4326"),
                request("TX-DAL-025", "Dallas", "EPSG:3857"),
                request("TX-AUS-025", "Austin duplicate", "EPSG:3857"),
                request("TX-HOU-025", "Houston", "EPSG:4326"),
                request("TX-DAL-025", "Dallas duplicate", "EPSG:4326")
        ));

        assertThat(plan.submittedCount()).isEqualTo(5);
        assertThat(plan.uniqueProjectCount()).isEqualTo(3);
        assertThat(plan.duplicateSubmissionCount()).isEqualTo(2);

        assertThat(plan.entries())
                .extracting(BatchProjectIntakeEntry::projectCode)
                .containsExactly(
                        "TX-AUS-025",
                        "TX-DAL-025",
                        "TX-HOU-025"
                );

        assertThat(plan.entries())
                .extracting(BatchProjectIntakeEntry::occurrenceCount)
                .containsExactly(2, 2, 1);
    }

    @Test
    void retainsFirstSeenMetadataForDuplicateProjectCode() {
        BatchProjectIntakePlan plan = planner.plan(List.of(
                request("TX-AUS-025", "First submission", "EPSG:4326"),
                request("TX-AUS-025", "Later duplicate", "EPSG:3857")
        ));

        BatchProjectIntakeEntry entry = plan.entries().get(0);

        assertThat(entry.name()).isEqualTo("First submission");
        assertThat(entry.coordinateReferenceSystem()).isEqualTo("EPSG:4326");
        assertThat(entry.occurrenceCount()).isEqualTo(2);
    }

    @Test
    void resultEntriesAreImmutable() {
        BatchProjectIntakePlan plan = planner.plan(List.of(
                request("TX-AUS-025", "Austin", "EPSG:4326")
        ));

        assertThat(plan.entries()).isUnmodifiable();
    }

    private CreateProjectRequest request(
            String projectCode,
            String name,
            String crs
    ) {
        return new CreateProjectRequest(projectCode, name, crs);
    }
}
