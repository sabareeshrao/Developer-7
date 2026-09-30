package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;

/**
 * Client-visible failure for malformed or unreadable project transfer data.
 */
public class ProjectDataTransferException extends GeoOpsProjectException {

    public ProjectDataTransferException(String message) {
        super(GeoOpsErrorCode.PROJECT_DATA_TRANSFER_FAILED, message);
    }

    public ProjectDataTransferException(String message, Throwable cause) {
        super(GeoOpsErrorCode.PROJECT_DATA_TRANSFER_FAILED, message, cause);
    }
}
