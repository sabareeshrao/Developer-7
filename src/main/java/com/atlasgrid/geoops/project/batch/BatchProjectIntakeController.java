package com.atlasgrid.geoops.project.batch;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST boundary for batch-intake reconciliation.
 */
@RestController
@RequestMapping("/api/projects/batch-plan")
public class BatchProjectIntakeController {

    private final BatchProjectIntakePlanner planner;

    public BatchProjectIntakeController(BatchProjectIntakePlanner planner) {
        this.planner = planner;
    }

    @PostMapping
    public BatchProjectIntakePlan plan(
            @Valid @RequestBody List<CreateProjectRequest> requests
    ) {
        return planner.plan(requests);
    }
}
