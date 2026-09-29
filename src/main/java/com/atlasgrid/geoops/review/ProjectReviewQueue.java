package com.atlasgrid.geoops.review;

import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory quality-review workflow for GIS projects.
 *
 * <p>Queued work remains a Deque backed by LinkedList because earlier Sets
 * established the head/tail worklist behavior. Access to that LinkedList is
 * protected by one internal queue lock so concurrent HTTP requests do not
 * mutate the non-thread-safe deque at the same time.</p>
 *
 * <p>Claimed work is stored in ConcurrentHashMap keyed by immutable String
 * projectCode. retry(...) and complete(...) both use atomic remove(key), so
 * concurrent transitions for the same claimed project have a single winner.</p>
 *
 * <p>This is a targeted in-memory concurrency hardening step, not a claim that
 * the whole GeoOps application is production-ready. ProjectCatalog concurrency
 * and transactional persistence remain separate future concerns.</p>
 */
@Component
public class ProjectReviewQueue {

    private final Object queueLock = new Object();
    private final Deque<ProjectReviewTask> reviewTasks = new LinkedList<>();
    private final Map<String, ProjectReviewTask> claimedTasksByProjectCode =
            new ConcurrentHashMap<>();

    public void enqueue(String projectCode) {
        synchronized (queueLock) {
            reviewTasks.addLast(new ProjectReviewTask(projectCode));
        }
    }

    public Optional<ProjectReviewTask> claimNext() {
        ProjectReviewTask task;

        synchronized (queueLock) {
            task = reviewTasks.pollFirst();
        }

        if (task == null) {
            return Optional.empty();
        }

        claimedTasksByProjectCode.put(task.projectCode(), task);
        return Optional.of(task);
    }

    public boolean retry(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        ProjectReviewTask task =
                claimedTasksByProjectCode.remove(projectCode);

        if (task == null) {
            return false;
        }

        synchronized (queueLock) {
            reviewTasks.addFirst(task);
        }

        return true;
    }

    public boolean complete(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        return claimedTasksByProjectCode.remove(projectCode) != null;
    }

    public boolean expedite(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        synchronized (queueLock) {
            Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

            while (iterator.hasNext()) {
                ProjectReviewTask task = iterator.next();

                if (task.projectCode().equals(projectCode)) {
                    iterator.remove();
                    reviewTasks.addFirst(task);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean defer(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        synchronized (queueLock) {
            Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

            while (iterator.hasNext()) {
                ProjectReviewTask task = iterator.next();

                if (task.projectCode().equals(projectCode)) {
                    iterator.remove();
                    reviewTasks.addLast(task);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean cancel(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        synchronized (queueLock) {
            Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

            while (iterator.hasNext()) {
                ProjectReviewTask task = iterator.next();

                if (task.projectCode().equals(projectCode)) {
                    iterator.remove();
                    return true;
                }
            }
        }

        return false;
    }

    public List<ProjectReviewTask> snapshot() {
        synchronized (queueLock) {
            return List.copyOf(reviewTasks);
        }
    }

    public int size() {
        synchronized (queueLock) {
            return reviewTasks.size();
        }
    }

    public int claimedTaskCount() {
        return claimedTasksByProjectCode.size();
    }
}
