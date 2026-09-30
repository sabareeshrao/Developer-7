package com.atlasgrid.geoops.tools.preflight;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Supported inbound dataset formats for the GeoOps preflight process.
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
        Path fileNamePath = dataset.getFileName();

        if (fileNamePath == null) {
            return false;
        }

        String fileName = fileNamePath
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
