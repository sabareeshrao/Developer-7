package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.project.validation.ParallelProjectValidationService;
import com.atlasgrid.geoops.project.validation.ValidationIntakeSwitch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> validateBatch(
            @RequestBody List<CreateProjectRequest> requests
    ) {
        if (!intakeSwitch.isOpen()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "code", "VALIDATION_UNAVAILABLE",
                            "message", "New validation requests are paused"
                    ));
        }
        return ResponseEntity.ok(validationService.validateAll(requests));
    }
}
