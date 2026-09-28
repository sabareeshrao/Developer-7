package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Composes all ProjectValidationRule implementations.
 *
 * <p>Spring injects every bean implementing ProjectValidationRule. The service
 * invokes the same validate method polymorphically without knowing the concrete
 * rule type. New rules can be added as new components without changing this
 * orchestration code.</p>
 */
@Service
public class ProjectValidationService {

    private final List<ProjectValidationRule> rules;

    public ProjectValidationService(List<ProjectValidationRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public ProjectValidationReport validate(CreateProjectRequest request) {
        List<ValidationIssue> issues = new ArrayList<>();

        for (ProjectValidationRule rule : rules) {
            issues.addAll(rule.validate(request));
        }

        return new ProjectValidationReport(issues.isEmpty(), issues);
    }
}
