package com.atlasgrid.geoops.project.batch;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.ProjectIdentity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Reconciles a batch of project-intake requests before actual project creation.
 *
 * <p>The planner needs two behaviors at the same time:</p>
 * <ul>
 *     <li>fast lookup by logical project identity to detect repeated codes;</li>
 *     <li>stable first-seen order so the resulting intake plan is predictable.</li>
 * </ul>
 *
 * <p>A LinkedHashMap satisfies both requirements. ProjectIdentity is immutable,
 * so the hash/equality state of a key cannot change after insertion.</p>
 */
@Service
public class BatchProjectIntakePlanner {

    public BatchProjectIntakePlan plan(
            Collection<CreateProjectRequest> requests
    ) {
        Objects.requireNonNull(requests, "requests");

        Map<ProjectIdentity, MutableBatchEntry> entriesByIdentity =
                new LinkedHashMap<>();

        for (CreateProjectRequest request : requests) {
            Objects.requireNonNull(request, "request");

            ProjectIdentity identity =
                    new ProjectIdentity(request.projectCode());

            entriesByIdentity.compute(
                    identity,
                    (ignored, existing) -> existing == null
                            ? new MutableBatchEntry(request)
                            : existing.increment()
            );
        }

        List<BatchProjectIntakeEntry> entries = new ArrayList<>();

        for (MutableBatchEntry entry : entriesByIdentity.values()) {
            entries.add(entry.toResult());
        }

        int submittedCount = requests.size();
        int uniqueProjectCount = entries.size();

        return new BatchProjectIntakePlan(
                submittedCount,
                uniqueProjectCount,
                submittedCount - uniqueProjectCount,
                entries
        );
    }

    private static final class MutableBatchEntry {

        private final CreateProjectRequest firstSeenRequest;
        private int occurrenceCount = 1;

        private MutableBatchEntry(CreateProjectRequest firstSeenRequest) {
            this.firstSeenRequest = firstSeenRequest;
        }

        private MutableBatchEntry increment() {
            occurrenceCount++;
            return this;
        }

        private BatchProjectIntakeEntry toResult() {
            return new BatchProjectIntakeEntry(
                    firstSeenRequest.projectCode(),
                    firstSeenRequest.name(),
                    firstSeenRequest.coordinateReferenceSystem(),
                    occurrenceCount
            );
        }
    }
}
