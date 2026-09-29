package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.batch.BatchProjectIntakePlan;
import com.atlasgrid.geoops.project.batch.BatchProjectIntakePlanner;
import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.review.ProjectReviewQueue;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Set 35 evidence proving the different Map roles already used by GeoOps.
 */
class ProjectMapUsageIntegrationTest {

    @Test
    void mapsServeDifferentWorkflowRequirementsWithoutBeingInterchangeable() {
        ProjectCollectionSummaryService summaryService =
                new ProjectCollectionSummaryService();

        ProjectCollectionSummary summary = summaryService.summarize(List.of(
                project("TX-AUS-035", "EPSG:4326"),
                project("TX-DAL-035", "EPSG:3857"),
                project("TX-HOU-035", "EPSG:4326")
        ));

        // LinkedHashMap: count by CRS while preserving first-seen key order.
        assertThat(summary.projectCountByCoordinateReferenceSystem())
                .containsExactly(
                        org.assertj.core.data.MapEntry.entry("EPSG:4326", 2),
                        org.assertj.core.data.MapEntry.entry("EPSG:3857", 1)
                );

        BatchProjectIntakePlanner planner =
                new BatchProjectIntakePlanner();

        BatchProjectIntakePlan plan = planner.plan(List.of(
                request("TX-AUS-035", "Austin first"),
                request("TX-DAL-035", "Dallas first"),
                request("TX-AUS-035", "Austin duplicate")
        ));

        // LinkedHashMap: reconcile by identity and retain first-seen order.
        assertThat(plan.entries())
                .extracting(entry -> entry.projectCode())
                .containsExactly("TX-AUS-035", "TX-DAL-035");
        assertThat(plan.entries().get(0).occurrenceCount()).isEqualTo(2);
        assertThat(plan.entries().get(0).name()).isEqualTo("Austin first");

        ProjectReviewQueue reviewQueue = new ProjectReviewQueue();
        reviewQueue.enqueue("TX-AUS-035");

        // HashMap: claimed state is keyed by projectCode; order is irrelevant.
        reviewQueue.claimNext().orElseThrow();

        assertThat(reviewQueue.claimedTaskCount()).isEqualTo(1);
        assertThat(reviewQueue.retry("TX-AUS-035")).isTrue();
        assertThat(reviewQueue.claimedTaskCount()).isZero();
        assertThat(reviewQueue.snapshot())
                .extracting(task -> task.projectCode())
                .containsExactly("TX-AUS-035");
    }

    private GeoProject project(String projectCode, String crs) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "Map Usage Project",
                crs,
                Instant.parse("2026-09-29T12:00:00Z")
        );
    }

    private CreateProjectRequest request(String projectCode, String name) {
        return new CreateProjectRequest(
                projectCode,
                name,
                "EPSG:4326"
        );
    }
}
