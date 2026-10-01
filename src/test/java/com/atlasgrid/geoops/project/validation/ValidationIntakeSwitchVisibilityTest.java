package com.atlasgrid.geoops.project.validation;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationIntakeSwitchVisibilityTest {
    @Test
    void pauseBecomesVisibleToAnotherThread() throws Exception {
        ValidationIntakeSwitch state = new ValidationIntakeSwitch();
        CountDownLatch ready = new CountDownLatch(1);
        var pool = Executors.newSingleThreadExecutor();
        try {
            var seen = pool.submit(() -> {
                ready.countDown();
                while (state.isOpen() && !Thread.currentThread().isInterrupted()) {
                    Thread.onSpinWait();
                }
                return !state.isOpen();
            });
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            state.pause();
            assertThat(seen.get(5, TimeUnit.SECONDS)).isTrue();
            state.resume();
            assertThat(state.isOpen()).isTrue();
        } finally {
            state.pause();
            pool.shutdownNow();
        }
    }
}
