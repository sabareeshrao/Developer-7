package com.atlasgrid.geoops.review;

import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory quality-review queue for newly created GIS projects.
 *
 * <p>The queue is declared through the Deque interface and currently backed by
 * LinkedList. The workflow needs efficient head/tail operations: new work is
 * appended at the tail, reviewers claim from the head, and a retry can be
 * pushed back to the front.</p>
 *
 * <p>When a queued project must be expedited, expedite(...) traverses to the
 * matching task, removes it through Iterator.remove(), and places that same task
 * at the front. Cancellation uses the same iterator-safe removal pattern.</p>
 *
 * <p>This component is intentionally not concurrent yet. A later concurrency
 * requirement can replace the implementation with an appropriate concurrent
 * queue without changing callers that depend on this abstraction.</p>
 */
@Component
public class ProjectReviewQueue {

    private final Deque<ProjectReviewTask> reviewTasks = new LinkedList<>();

    public void enqueue(String projectCode) {
        reviewTasks.addLast(new ProjectReviewTask(projectCode));
    }

    public Optional<ProjectReviewTask> claimNext() {
        return Optional.ofNullable(reviewTasks.pollFirst());
    }

    public void retryFirst(ProjectReviewTask task) {
        reviewTasks.addFirst(Objects.requireNonNull(task, "task"));
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
}
