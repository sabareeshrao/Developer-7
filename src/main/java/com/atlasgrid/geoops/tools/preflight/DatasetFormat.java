package com.atlasgrid.geoops.tools.preflight;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Supported inbound dataset formats for the GeoOps preflight process.
 *
 * <p>An enum is appropriate because the supported formats are a fixed,
 * well-known set. Each constant owns its extension and the enum can provide
 * shared behavior without scattering string literals through the validator.</p>
 */
public enum DatasetFormat {

    CSV(".csv"),
    JSON(".json"),
    GEOJSON(".geojson");

    private final String extension;

    DatasetFormat(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }

    public boolean matches(Path dataset) {
        String fileName = dataset.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT);

        return fileName.endsWith(extension);
    }

    public static boolean supports(Path dataset) {
        for (DatasetFormat format : values()) {
            if (format.matches(dataset)) {
                return true;
            }
        }
        return false;
    }

    public static String supportedExtensions() {
        StringBuilder extensions = new StringBuilder();

        for (DatasetFormat format : values()) {
            if (!extensions.isEmpty()) {
                extensions.append(", ");
            }
            extensions.append(format.extension());
        }

        return extensions.toString();
    }
}
