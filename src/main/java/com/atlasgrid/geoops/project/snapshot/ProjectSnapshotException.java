package com.atlasgrid.geoops.project.snapshot;

import java.util.Objects;

/**
 * Application exception for the trusted internal snapshot format.
 */
public class ProjectSnapshotException extends RuntimeException {

    private final ProjectSnapshotFailure failure;

    public ProjectSnapshotException(
            ProjectSnapshotFailure failure,
            String message
    ) {
        super(message);
        this.failure = Objects.requireNonNull(failure, "failure");
    }

    public ProjectSnapshotException(
            ProjectSnapshotFailure failure,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.failure = Objects.requireNonNull(failure, "failure");
    }

    public ProjectSnapshotFailure failure() {
        return failure;
    }
}
