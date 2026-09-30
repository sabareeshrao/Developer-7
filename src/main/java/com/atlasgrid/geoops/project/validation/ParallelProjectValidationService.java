package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Runs independent project-request validation on a bounded worker pool.
 *
 * <p>The service preserves input order in its returned results and does not
 * mutate the project catalog.</p>
 */
@Service
public class ParallelProjectValidationService {

    private final ProjectValidationService validationService;
    private final ExecutorService executorService;

    public ParallelProjectValidationService(
            ProjectValidationService validationService,
            @Qualifier("projectValidationExecutor")
            ExecutorService executorService
    ) {
        this.validationService = validationService;
        this.executorService = executorService;
    }

    public List<ProjectBatchValidationResult> validateAll(
            List<CreateProjectRequest> requests
    ) {
        Objects.requireNonNull(requests, "requests");

        List<CreateProjectRequest> copy = List.copyOf(requests);
        List<Future<ProjectBatchValidationResult>> futures =
                new ArrayList<>(copy.size());

        for (CreateProjectRequest request : copy) {
            futures.add(
                    executorService.submit(
                            () -> validateOne(request)
                    )
            );
        }

        List<ProjectBatchValidationResult> results =
                new ArrayList<>(futures.size());

        for (Future<ProjectBatchValidationResult> future : futures) {
            results.add(await(future));
        }

        return List.copyOf(results);
    }

    private ProjectBatchValidationResult validateOne(
            CreateProjectRequest request
    ) {
        ProjectValidationReport report =
                validationService.validate(request);

        return new ProjectBatchValidationResult(
                request.projectCode(),
                report.valid(),
                report.issues()
        );
    }

    private ProjectBatchValidationResult await(
            Future<ProjectBatchValidationResult> future
    ) {
        try {
            return future.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Parallel project validation was interrupted",
                    exception
            );
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new IllegalStateException(
                    "Parallel project validation failed",
                    cause
            );
        }
    }
}
