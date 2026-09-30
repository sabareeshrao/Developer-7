package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.validation.ParallelProjectValidationService;
import com.atlasgrid.geoops.project.validation.ProjectBatchValidationResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only batch validation boundary for preflighting independent GIS intake
 * requests concurrently.
 */
@RestController
@RequestMapping("/api/projects/validation")
public class ProjectBatchValidationController {

    private final ParallelProjectValidationService validationService;

    public ProjectBatchValidationController(
            ParallelProjectValidationService validationService
    ) {
        this.validationService = validationService;
    }

    @PostMapping("/batch")
    public List<ProjectBatchValidationResult> validateBatch(
            @RequestBody List<CreateProjectRequest> requests
    ) {
        return validationService.validateAll(requests);
    }
}
