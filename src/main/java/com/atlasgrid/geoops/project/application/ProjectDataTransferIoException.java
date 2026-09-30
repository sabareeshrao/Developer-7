package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;

/**
 * Server-side I/O failure while reading or writing a project transfer.
 */
public class ProjectDataTransferIoException extends GeoOpsProjectException {

    public ProjectDataTransferIoException(String message, Throwable cause) {
        super(
                GeoOpsErrorCode.PROJECT_DATA_TRANSFER_IO_FAILED,
                message,
                cause
        );
    }
}
