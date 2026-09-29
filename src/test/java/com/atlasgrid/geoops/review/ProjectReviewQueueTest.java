package com.atlasgrid.geoops.review;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectReviewQueueTest {

    @Test
    void claimsProjectsInFifoOrderAndIndexesClaimedTasks() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-034");
        queue.enqueue("TX-DAL-034");

        assertThat(queue.claimNext())
                .isPresent()
                .get()
                .extracting(ProjectReviewTask::projectCode)
                .isEqualTo("TX-AUS-034");
        assertThat(queue.claimedTaskCount()).isEqualTo(1);

        assertThat(queue.claimNext())
                .isPresent()
                .get()
                .extracting(ProjectReviewTask::projectCode)
                .isEqualTo("TX-DAL-034");
        assertThat(queue.claimedTaskCount()).isEqualTo(2);
    }

    @Test
    void retryRequiresAnActuallyClaimedTask() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-034");
        queue.enqueue("TX-DAL-034");

        queue.claimNext().orElseThrow();

        assertThat(queue.retry("TX-AUS-034")).isTrue();
        assertThat(queue.retry("TX-HOU-034")).isFalse();
        assertThat(queue.claimedTaskCount()).isZero();
        assertThat(queue.snapshot())
                .extracting(ProjectReviewTask::projectCode)
                .containsExactly("TX-AUS-034", "TX-DAL-034");
    }

    @Test
    void completeRemovesClaimedTaskAndPreventsLaterRetry() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-034");
        queue.claimNext().orElseThrow();

        assertThat(queue.complete("TX-AUS-034")).isTrue();
        assertThat(queue.complete("TX-AUS-034")).isFalse();
        assertThat(queue.retry("TX-AUS-034")).isFalse();
        assertThat(queue.claimedTaskCount()).isZero();
    }

    @Test
    void retryAndCompleteCompetingForSameClaimHaveSingleWinner()
            throws InterruptedException {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-037");
        queue.claimNext().orElseThrow();

        CountDownLatch start = new CountDownLatch(1);
        AtomicBoolean retryResult = new AtomicBoolean();
        AtomicBoolean completeResult = new AtomicBoolean();

        Thread retryThread = new Thread(() -> {
            await(start);
            retryResult.set(queue.retry("TX-AUS-037"));
        });

        Thread completeThread = new Thread(() -> {
            await(start);
            completeResult.set(queue.complete("TX-AUS-037"));
        });

        retryThread.start();
        completeThread.start();
        start.countDown();

        retryThread.join();
        completeThread.join();

        assertThat(retryResult.get() ^ completeResult.get()).isTrue();
        assertThat(queue.claimedTaskCount()).isZero();

        if (retryResult.get()) {
            assertThat(queue.snapshot())
                    .extracting(ProjectReviewTask::projectCode)
                    .containsExactly("TX-AUS-037");
        } else {
            assertThat(queue.snapshot()).isEmpty();
        }
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
    void defersQueuedProjectToTailWithoutDuplicatingIt() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-032");
        queue.enqueue("TX-DAL-032");
        queue.enqueue("TX-HOU-032");

        assertThat(queue.defer("TX-AUS-032")).isTrue();
        assertThat(queue.snapshot())
                .extracting(ProjectReviewTask::projectCode)
                .containsExactly("TX-DAL-032", "TX-HOU-032", "TX-AUS-032");
        assertThat(queue.size()).isEqualTo(3);
    }

    @Test
    void deferringUnknownProjectLeavesQueueUnchanged() {
        ProjectReviewQueue queue = new ProjectReviewQueue();

        queue.enqueue("TX-AUS-032");
        queue.enqueue("TX-DAL-032");

        assertThat(queue.defer("TX-HOU-032")).isFalse();
        assertThat(queue.snapshot())
                .extracting(ProjectReviewTask::projectCode)
                .containsExactly("TX-AUS-032", "TX-DAL-032");
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

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
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
