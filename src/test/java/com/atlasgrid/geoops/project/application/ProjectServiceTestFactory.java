package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.validation.CoordinateReferenceSystemValidationRule;
import com.atlasgrid.geoops.project.validation.ProjectCodeValidationRule;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;

import java.util.List;

final class ProjectServiceTestFactory {

    private ProjectServiceTestFactory() {
    }

    static ProjectService create() {
        ProjectValidationService validationService =
                new ProjectValidationService(
                        List.of(
                                new ProjectCodeValidationRule(),
                                new CoordinateReferenceSystemValidationRule()
                        )
                );

        return new ProjectService(validationService);
    }
}
