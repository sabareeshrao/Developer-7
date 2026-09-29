package com.atlasgrid.geoops.tools.preflight;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * Lightweight command-line preflight validation for inbound GIS data files.
 *
 * <p>This validator deliberately contains no System.exit call. Business logic
 * returns a result; only the outer CLI boundary decides whether the JVM process
 * should terminate with a non-zero status.</p>
 *
 * <p>Path parsing/access failures are handled locally because the CLI can
 * convert them directly into a meaningful preflight failure result.</p>
 */
public class DatasetPreflightValidator {

    public PreflightResult validate(String[] args) {
        if (args == null || args.length != 1 || args[0].isBlank()) {
            return PreflightResult.failure(
                    2,
                    "Usage: GeoOpsPreflightCli <dataset-file>"
            );
        }

        try {
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

            if (!DatasetFormat.supports(dataset)) {
                return PreflightResult.failure(
                        5,
                        "Unsupported dataset type. Supported: "
                                + DatasetFormat.supportedExtensions()
                );
            }

            return PreflightResult.success(
                    "Dataset passed basic GeoOps preflight checks: " + dataset
            );
        } catch (InvalidPathException | SecurityException exception) {
            return PreflightResult.failure(
                    6,
                    "Dataset path is invalid or inaccessible"
            );
        }
    }
}
