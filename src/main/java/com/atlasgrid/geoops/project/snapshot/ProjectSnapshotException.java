package com.atlasgrid.geoops.project.snapshot;

/**
 * Application exception for the trusted internal snapshot format.
 */
public class ProjectSnapshotException extends RuntimeException {

    public ProjectSnapshotException(String message) {
        super(message);
    }

    public ProjectSnapshotException(String message, Throwable cause) {
        super(message, cause);
    }
}
