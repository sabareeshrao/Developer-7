package com.atlasgrid.geoops.review;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST boundary for the current in-memory project quality-review workflow.
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

    @PostMapping("/{projectCode}/retry")
    public ResponseEntity<Void> retry(@PathVariable String projectCode) {
        return projectReviewQueue.retry(projectCode)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{projectCode}/complete")
    public ResponseEntity<Void> complete(@PathVariable String projectCode) {
        return projectReviewQueue.complete(projectCode)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{projectCode}/expedite")
    public ResponseEntity<Void> expedite(@PathVariable String projectCode) {
        return projectReviewQueue.expedite(projectCode)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{projectCode}/defer")
    public ResponseEntity<Void> defer(@PathVariable String projectCode) {
        return projectReviewQueue.defer(projectCode)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{projectCode}")
    public ResponseEntity<Void> cancel(@PathVariable String projectCode) {
        return projectReviewQueue.cancel(projectCode)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
