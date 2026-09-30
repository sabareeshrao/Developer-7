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
 * <p>One state lock protects compound transitions between queued and claimed
 * states. ConcurrentHashMap remains the claimed-task store established by the
 * earlier concurrency Set, while the lock makes queue↔claimed transitions
 * atomic from the workflow's point of view.</p>
 */
@Component
public class ProjectReviewQueue {

    private final Object stateLock = new Object();
    private final Deque<ProjectReviewTask> reviewTasks = new LinkedList<>();
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

        synchronized (stateLock) {
            for (ProjectReviewTask task : tasks) {
                reviewTasks.addLast(task);
            }
        }
    }

    public Optional<ProjectReviewTask> claimNext() {
        synchronized (stateLock) {
            ProjectReviewTask task = reviewTasks.pollFirst();

            if (task == null) {
                return Optional.empty();
            }

            claimedTasksByProjectCode.put(task.projectCode(), task);
            return Optional.of(task);
        }
    }

    public boolean retry(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        synchronized (stateLock) {
            ProjectReviewTask task =
                    claimedTasksByProjectCode.remove(projectCode);

            if (task == null) {
                return false;
            }

            reviewTasks.addFirst(task);
            return true;
        }
    }

    public boolean complete(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        synchronized (stateLock) {
            return claimedTasksByProjectCode.remove(projectCode) != null;
        }
    }

    public boolean expedite(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        synchronized (stateLock) {
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

        synchronized (stateLock) {
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

        synchronized (stateLock) {
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
        synchronized (stateLock) {
            return List.copyOf(reviewTasks);
        }
    }

    public int size() {
        synchronized (stateLock) {
            return reviewTasks.size();
        }
    }

    public int claimedTaskCount() {
        synchronized (stateLock) {
            return claimedTasksByProjectCode.size();
        }
    }
}
