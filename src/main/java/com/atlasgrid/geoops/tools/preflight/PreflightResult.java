package com.atlasgrid.geoops.tools.preflight;

/**
 * Result returned by the standalone GIS dataset preflight validator.
 *
 * @param valid    whether the input passed validation
 * @param exitCode operating-system process exit code
 * @param message  human-readable result
 */
public record PreflightResult(
        boolean valid,
        int exitCode,
        String message
) {
    public static PreflightResult success(String message) {
        return new PreflightResult(true, 0, message);
    }

    public static PreflightResult failure(int exitCode, String message) {
        return new PreflightResult(false, exitCode, message);
    }
}
