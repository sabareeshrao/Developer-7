package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationTraceContextTest {

    @Test
    void workerReuseNeverLeaksPreviousProjectCode() throws Exception {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        List<String> observed = new CopyOnWriteArrayList<>();
        ProjectValidationRule rule = new ProjectValidationRule() {
            @Override
            public String code() {
                return "OBSERVE_VALIDATION_TRACE";
            }

            @Override
            public List<ValidationIssue> validate(
                    CreateProjectRequest request
            ) {
                observed.add(ValidationTraceContext.currentProjectCode());
                return List.of();
            }
        };

        try {
            ParallelProjectValidationService service =
                    new ParallelProjectValidationService(
                            new ProjectValidationService(List.of(rule)),
                            worker
                    );
            service.validateAll(List.of(
                    new CreateProjectRequest(
                            "TX-AUS-821", "First", "EPSG:4326"
                    ),
                    new CreateProjectRequest(
                            "TX-AUS-822", "Second", "EPSG:4326"
                    )
            ));
            assertThat(observed).containsExactly(
                    "TX-AUS-821", "TX-AUS-822"
            );
            assertThat(worker.submit(
                    ValidationTraceContext::currentProjectCode
            ).get(5, TimeUnit.SECONDS)).isNull();
        } finally {
            worker.shutdownNow();
        }
    }

    @Test
    void contextIsRemovedAfterWorkerThrows() throws Exception {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        ProjectValidationRule failing = new ProjectValidationRule() {
            @Override
            public String code() {
                return "FAIL_AFTER_TRACE";
            }

            @Override
            public List<ValidationIssue> validate(
                    CreateProjectRequest request
            ) {
                assertThat(
                        ValidationTraceContext.currentProjectCode()
                ).isEqualTo("TX-AUS-823");
                throw new IllegalArgumentException("Controlled failure");
            }
        };
        try {
            ParallelProjectValidationService service =
                    new ParallelProjectValidationService(
                            new ProjectValidationService(List.of(failing)),
                            worker
                    );
            assertThatThrownBy(() -> service.validateAll(List.of(
                    new CreateProjectRequest(
                            "TX-AUS-823", "Failing", "EPSG:4326"
                    )
            ))).isInstanceOf(IllegalArgumentException.class);
            assertThat(worker.submit(
                    ValidationTraceContext::currentProjectCode
            ).get(5, TimeUnit.SECONDS)).isNull();
        } finally {
            worker.shutdownNow();
        }
    }
}
