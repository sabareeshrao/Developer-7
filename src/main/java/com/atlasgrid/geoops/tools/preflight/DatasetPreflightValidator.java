package com.atlasgrid.geoops.tools.preflight;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

/**
 * Lightweight command-line preflight validation for inbound GIS data files.
 *
 * <p>This validator deliberately contains no System.exit call. Business logic
 * returns a result; only the outer CLI boundary decides whether the JVM process
 * should terminate with a non-zero status.</p>
 */
public class DatasetPreflightValidator {

    private static final Set<String> SUPPORTED_EXTENSIONS =
            Set.of(".csv", ".json", ".geojson");

    public PreflightResult validate(String[] args) {
        if (args == null || args.length != 1 || args[0].isBlank()) {
            return PreflightResult.failure(
                    2,
                    "Usage: GeoOpsPreflightCli <dataset-file>"
            );
        }

        Path dataset = Path.of(args[0]);

        if (!Files.exists(dataset)) {
            return PreflightResult.failure(
                    3,
                    "Dataset does not exist: " + dataset
            );
        }

        if (!Files.isRegularFile(dataset)) {
            return PreflightResult.failure(
                    4,
                    "Dataset path is not a regular file: " + dataset
            );
        }

        String fileName = dataset.getFileName().toString().toLowerCase(Locale.ROOT);
        boolean supported = SUPPORTED_EXTENSIONS.stream().anyMatch(fileName::endsWith);

        if (!supported) {
            return PreflightResult.failure(
                    5,
                    "Unsupported dataset type. Supported: " + SUPPORTED_EXTENSIONS
            );
        }

        return PreflightResult.success(
                "Dataset passed basic GeoOps preflight checks: " + dataset
        );
    }
}
