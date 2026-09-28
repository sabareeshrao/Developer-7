package com.atlasgrid.geoops.tools.preflight;

/**
 * Standalone process used by batch/Jenkins-style workflows before an inbound
 * GIS dataset is submitted to GeoOps.
 *
 * <p>System.exit is intentionally restricted to this outer application
 * boundary. Controllers, services and domain logic must not terminate the JVM.</p>
 */
public final class GeoOpsPreflightCli {

    private GeoOpsPreflightCli() {
    }

    public static void main(String[] args) {
        DatasetPreflightValidator validator = new DatasetPreflightValidator();
        PreflightResult result = validator.validate(args);

        if (result.valid()) {
            System.out.println(result.message());
            return;
        }

        System.err.println(result.message());
        System.exit(result.exitCode());
    }
}
