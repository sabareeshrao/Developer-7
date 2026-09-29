package com.atlasgrid.geoops.review;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectReviewQueueTest {

    @Test
    void claimsProjectsInFifoOrder() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-024");
        queue.enqueue("TX-DAL-024");
        queue.enqueue("TX-HOU-024");

        assertThat(queue.claimNext())
                .isPresent()
                .get()
                .extracting(ProjectReviewTask::projectCode)
                .isEqualTo("TX-AUS-024");

        assertThat(queue.claimNext())
                .isPresent()
                .get()
                .extracting(ProjectReviewTask::projectCode)
                .isEqualTo("TX-DAL-024");
    }

    @Test
    void retryCanBeMovedToFront() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-024");
        queue.enqueue("TX-DAL-024");

        ProjectReviewTask first = queue.claimNext().orElseThrow();
        queue.retryFirst(first);

        assertThat(queue.snapshot())
                .extracting(ProjectReviewTask::projectCode)
                .containsExactly("TX-AUS-024", "TX-DAL-024");
    }

    @Test
    void expeditesQueuedProjectToFrontWithoutDuplicatingIt() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-031");
        queue.enqueue("TX-DAL-031");
        queue.enqueue("TX-HOU-031");

        assertThat(queue.expedite("TX-HOU-031")).isTrue();
        assertThat(queue.snapshot())
                .extracting(ProjectReviewTask::projectCode)
                .containsExactly("TX-HOU-031", "TX-AUS-031", "TX-DAL-031");
        assertThat(queue.size()).isEqualTo(3);
    }

    @Test
    void expeditingUnknownProjectLeavesQueueUnchanged() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-031");
        queue.enqueue("TX-DAL-031");

        assertThat(queue.expedite("TX-HOU-031")).isFalse();
        assertThat(queue.snapshot())
                .extracting(ProjectReviewTask::projectCode)
                .containsExactly("TX-AUS-031", "TX-DAL-031");
    }

    @Test
    void cancelUsesIteratorSafeRemovalAndKeepsRemainingOrder() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-024");
        queue.enqueue("TX-DAL-024");
        queue.enqueue("TX-HOU-024");

        assertThat(queue.cancel("TX-DAL-024")).isTrue();
        assertThat(queue.snapshot())
                .extracting(ProjectReviewTask::projectCode)
                .containsExactly("TX-AUS-024", "TX-HOU-024");
    }

    @Test
    void snapshotDoesNotExposeMutableLinkedList() {
        ProjectReviewQueue queue = new ProjectReviewQueue();
        queue.enqueue("TX-AUS-024");

        assertThat(queue.snapshot())
                .hasSize(1)
                .isUnmodifiable();
    }
}
