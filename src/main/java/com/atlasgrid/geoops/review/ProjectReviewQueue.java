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
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory quality-review workflow for GIS projects.
 *
 * <p>One ReentrantLock protects compound transitions between queued and
 * claimed states. ConcurrentHashMap remains the claimed-task store established
 * by the earlier concurrency Set, while the lock makes queue↔claimed
 * transitions atomic from the workflow's point of view.</p>
 */
@Component
public class ProjectReviewQueue {

    private final ReentrantLock stateLock =
            new ReentrantLock();
    private final Deque<ProjectReviewTask> reviewTasks =
            new LinkedList<>();
    private final Map<String, ProjectReviewTask> claimedTasksByProjectCode =
            new ConcurrentHashMap<>();

    public void enqueue(String projectCode) {
        enqueueAll(List.of(projectCode));
    }

    public void enqueueAll(List<String> projectCodes) {
        Objects.requireNonNull(projectCodes, "projectCodes");

        List<ProjectReviewTask> tasks = projectCodes.stream()
                .map(ProjectReviewTask::new)
                .toList();

        stateLock.lock();

        try {
            for (ProjectReviewTask task : tasks) {
                reviewTasks.addLast(task);
            }
        } finally {
            stateLock.unlock();
        }
    }

    public Optional<ProjectReviewTask> claimNext() {
        stateLock.lock();

        try {
            ProjectReviewTask task = reviewTasks.pollFirst();

            if (task == null) {
                return Optional.empty();
            }

            claimedTasksByProjectCode.put(task.projectCode(), task);
            return Optional.of(task);
        } finally {
            stateLock.unlock();
        }
    }

    public boolean retry(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        stateLock.lock();

        try {
            ProjectReviewTask task =
                    claimedTasksByProjectCode.remove(projectCode);

            if (task == null) {
                return false;
            }

            reviewTasks.addFirst(task);
            return true;
        } finally {
            stateLock.unlock();
        }
    }

    public boolean complete(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        stateLock.lock();

        try {
            return claimedTasksByProjectCode.remove(projectCode) != null;
        } finally {
            stateLock.unlock();
        }
    }

    public boolean expedite(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        stateLock.lock();

        try {
            Iterator<ProjectReviewTask> iterator =
                    reviewTasks.iterator();

            while (iterator.hasNext()) {
                ProjectReviewTask task = iterator.next();

                if (task.projectCode().equals(projectCode)) {
                    iterator.remove();
                    reviewTasks.addFirst(task);
                    return true;
                }
            }

            return false;
        } finally {
            stateLock.unlock();
        }
    }

    public boolean defer(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        stateLock.lock();

        try {
            Iterator<ProjectReviewTask> iterator =
                    reviewTasks.iterator();

            while (iterator.hasNext()) {
                ProjectReviewTask task = iterator.next();

                if (task.projectCode().equals(projectCode)) {
                    iterator.remove();
                    reviewTasks.addLast(task);
                    return true;
                }
            }

            return false;
        } finally {
            stateLock.unlock();
        }
    }

    public boolean cancel(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        stateLock.lock();

        try {
            Iterator<ProjectReviewTask> iterator =
                    reviewTasks.iterator();

            while (iterator.hasNext()) {
                ProjectReviewTask task = iterator.next();

                if (task.projectCode().equals(projectCode)) {
                    iterator.remove();
                    return true;
                }
            }

            return false;
        } finally {
            stateLock.unlock();
        }
    }

    public List<ProjectReviewTask> snapshot() {
        stateLock.lock();

        try {
            return List.copyOf(reviewTasks);
        } finally {
            stateLock.unlock();
        }
    }

    public int size() {
        stateLock.lock();

        try {
            return reviewTasks.size();
        } finally {
            stateLock.unlock();
        }
    }

    public int claimedTaskCount() {
        stateLock.lock();

        try {
            return claimedTasksByProjectCode.size();
        } finally {
            stateLock.unlock();
        }
    }
}
