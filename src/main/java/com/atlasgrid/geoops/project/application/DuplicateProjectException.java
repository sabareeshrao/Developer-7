package com.atlasgrid.geoops.project.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Raised when a logical project identity already exists in the current
 * in-memory GeoOps registry.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateProjectException extends RuntimeException {

    public DuplicateProjectException(String projectCode) {
        super("Project already exists for projectCode=" + projectCode);
    }
}
