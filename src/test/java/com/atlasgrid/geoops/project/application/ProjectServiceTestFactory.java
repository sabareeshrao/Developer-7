package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.validation.CoordinateReferenceSystemValidationRule;
import com.atlasgrid.geoops.project.validation.ProjectValidationRuleConfiguration;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;
import com.atlasgrid.geoops.review.ProjectReviewQueue;

import java.util.List;

final class ProjectServiceTestFactory {

    private ProjectServiceTestFactory() {
    }

    static ProjectService create() {
        ProjectValidationService validationService =
                new ProjectValidationService(
                        List.of(
                                new ProjectValidationRuleConfiguration().projectCodeValidationRule(),
                                new CoordinateReferenceSystemValidationRule()
                        )
                );

        return new ProjectService(
                validationService,
                new ProjectCatalog(),
                new ProjectReviewQueue()
        );
    }
}
