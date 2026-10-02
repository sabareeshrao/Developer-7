package com.atlasgrid.geoops.diagnostics.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Controlled deadlock demonstration. Run in its own Java process, NEVER inside
 * the GeoOps web server or an existing JVM carrying application work.
 */
public final class DeadlockInvestigationCli {

    private DeadlockInvestigationCli() {
        throw new IllegalStateException("Utility class");
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock catalog = new ReentrantLock();
        ReentrantLock review = new ReentrantLock();
        CountDownLatch firstLocksAcquired = new CountDownLatch(2);

        Thread first = new Thread(() -> acquireInOrder(
                catalog, review, firstLocksAcquired
        ), "geoops-deadlock-lab-catalog");

        Thread second = new Thread(() -> acquireInOrder(
                review, catalog, firstLocksAcquired
        ), "geoops-deadlock-lab-review");

        first.setDaemon(true);
        second.setDaemon(true);
        first.start();
        second.start();

        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] ids = null;

        for (int attempt = 0; attempt < 100; attempt++) {
            ids = bean.findDeadlockedThreads();
            if (ids != null && ids.length >= 2) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(25);
        }

        if (ids == null || ids.length < 2) {
            throw new IllegalStateException("Deadlock was not observed");
        }

        for (ThreadInfo info : bean.getThreadInfo(ids, 20)) {
            if (info != null) {
                System.out.println(
                        info.getThreadName() + " | "
                                + info.getThreadState() + " | waits for "
                                + info.getLockName() + " owned by "
                                + info.getLockOwnerName()
                );
            }
        }
    }

    private static void acquireInOrder(
            ReentrantLock first,
            ReentrantLock second,
            CountDownLatch acquired
    ) {
        first.lock();
        try {
            acquired.countDown();

            try {
                acquired.await();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            }

            second.lock();
            try {
                System.out.println("Both locks acquired");
            } finally {
                second.unlock();
            }
        } finally {
            first.unlock();
        }
    }
}
