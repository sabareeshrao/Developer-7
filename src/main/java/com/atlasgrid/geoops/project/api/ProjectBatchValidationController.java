package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.validation.ParallelProjectValidationService;
import com.atlasgrid.geoops.project.validation.ProjectBatchValidationResult;
import com.atlasgrid.geoops.project.validation.ValidationIntakeSwitch;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/validation")
public class ProjectBatchValidationController {

    private final ParallelProjectValidationService validationService;
    private final ValidationIntakeSwitch intakeSwitch;

    public ProjectBatchValidationController(
            ParallelProjectValidationService validationService,
            ValidationIntakeSwitch intakeSwitch
    ) {
        this.validationService = validationService;
        this.intakeSwitch = intakeSwitch;
    }

    @PostMapping("/batch")
    public List<ProjectBatchValidationResult> validateBatch(
            @RequestBody List<CreateProjectRequest> requests
    ) {
        if (!intakeSwitch.isOpen()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "New validation requests are paused"
            );
        }
        return validationService.validateAll(requests);
    }
}
