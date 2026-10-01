package com.atlasgrid.geoops.review;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectReviewQueueReentrantLockTest {

    @Test
    void reviewQueueUsesNonFairReentrantLockAndReleasesIt()
            throws Exception {
        ProjectReviewQueue queue =
                new ProjectReviewQueue();

        Field field =
                ProjectReviewQueue.class
                        .getDeclaredField("stateLock");
        field.setAccessible(true);

        ReentrantLock lock =
                (ReentrantLock) field.get(queue);

        assertThat(lock.isFair()).isFalse();
        assertThat(lock.isLocked()).isFalse();

        queue.enqueue("TX-AUS-750");
        assertThat(queue.claimNext()).isPresent();
        assertThat(queue.complete("TX-AUS-750")).isTrue();

        assertThat(lock.isLocked()).isFalse();
        assertThat(lock.getHoldCount()).isZero();
    }
}
