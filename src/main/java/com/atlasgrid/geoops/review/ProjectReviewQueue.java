package com.atlasgrid.geoops.review;

import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory quality-review workflow for GIS projects.
 *
 * <p>Queued work is stored through Deque and backed by LinkedList because the
 * workflow uses head/tail operations and occasional reordering. Claimed work is
 * stored in a HashMap keyed by immutable String projectCode so retry/complete
 * operations can validate claimed state through expected O(1) average lookup.</p>
 *
 * <p>This component is intentionally not concurrent yet. A later concurrency
 * requirement can replace the in-memory structures with appropriate
 * thread-safe/persistent implementations.</p>
 */
@Component
public class ProjectReviewQueue {

    private final Deque<ProjectReviewTask> reviewTasks = new LinkedList<>();
    private final Map<String, ProjectReviewTask> claimedTasksByProjectCode =
            new HashMap<>();

    public void enqueue(String projectCode) {
        reviewTasks.addLast(new ProjectReviewTask(projectCode));
    }

    public Optional<ProjectReviewTask> claimNext() {
        ProjectReviewTask task = reviewTasks.pollFirst();

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

        reviewTasks.addFirst(task);
        return true;
    }

    public boolean complete(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        return claimedTasksByProjectCode.remove(projectCode) != null;
    }

    public boolean expedite(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

        while (iterator.hasNext()) {
            ProjectReviewTask task = iterator.next();

            if (task.projectCode().equals(projectCode)) {
                iterator.remove();
                reviewTasks.addFirst(task);
                return true;
            }
        }

        return false;
    }

    public boolean defer(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

        while (iterator.hasNext()) {
            ProjectReviewTask task = iterator.next();

            if (task.projectCode().equals(projectCode)) {
                iterator.remove();
                reviewTasks.addLast(task);
                return true;
            }
        }

        return false;
    }

    public boolean cancel(String projectCode) {
        Objects.requireNonNull(projectCode, "projectCode");

        Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

        while (iterator.hasNext()) {
            ProjectReviewTask task = iterator.next();

            if (task.projectCode().equals(projectCode)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public List<ProjectReviewTask> snapshot() {
        return List.copyOf(reviewTasks);
    }

    public int size() {
        return reviewTasks.size();
    }

    public int claimedTaskCount() {
        return claimedTasksByProjectCode.size();
    }
}
