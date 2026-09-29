package com.atlasgrid.geoops.review;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST boundary for the current in-memory project quality-review queue.
 */
@RestController
@RequestMapping("/api/review-queue")
public class ProjectReviewQueueController {

    private final ProjectReviewQueue projectReviewQueue;

    public ProjectReviewQueueController(ProjectReviewQueue projectReviewQueue) {
        this.projectReviewQueue = projectReviewQueue;
    }

    @GetMapping
    public List<ProjectReviewTask> getQueue() {
        return projectReviewQueue.snapshot();
    }

    @PostMapping("/claim-next")
    public ResponseEntity<ProjectReviewTask> claimNext() {
        return ResponseEntity.of(projectReviewQueue.claimNext());
    }

    @PostMapping("/retry-first")
    public void retryFirst(@RequestBody ProjectReviewTask task) {
        projectReviewQueue.retryFirst(task);
    }

    @DeleteMapping("/{projectCode}")
    public ResponseEntity<Void> cancel(@PathVariable String projectCode) {
        return projectReviewQueue.cancel(projectCode)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
