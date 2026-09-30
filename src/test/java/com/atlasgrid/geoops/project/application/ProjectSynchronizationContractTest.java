package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.validation.ProjectValidationService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectSynchronizationContractTest {

    @Test
    void projectCatalogProtectsSharedCollectionMethodsWithSynchronized()
            throws Exception {
        List<Method> protectedMethods = List.of(
                ProjectCatalog.class.getDeclaredMethod(
                        "add",
                        com.atlasgrid.geoops.project.domain.GeoProject.class
                ),
                ProjectCatalog.class.getDeclaredMethod(
                        "addAllAtomically",
                        List.class
                ),
                ProjectCatalog.class.getDeclaredMethod("findAll"),
                ProjectCatalog.class.getDeclaredMethod("size")
        );

        assertThat(protectedMethods)
                .allSatisfy(method ->
                        assertThat(
                                Modifier.isSynchronized(
                                        method.getModifiers()
                                )
                        ).isTrue()
                );
    }

    @Test
    void statelessValidationMethodDoesNotNeedMethodLevelSynchronization()
            throws Exception {
        Method validate =
                ProjectValidationService.class.getDeclaredMethod(
                        "validate",
                        CreateProjectRequest.class
                );

        assertThat(
                Modifier.isSynchronized(validate.getModifiers())
        ).isFalse();
    }

    @Test
    void projectServiceUsesNarrowCriticalSectionInsteadOfSynchronizingWholeMethod()
            throws Exception {
        Method create =
                ProjectService.class.getDeclaredMethod(
                        "create",
                        CreateProjectRequest.class
                );

        assertThat(
                Modifier.isSynchronized(create.getModifiers())
        ).isFalse();
    }
}
