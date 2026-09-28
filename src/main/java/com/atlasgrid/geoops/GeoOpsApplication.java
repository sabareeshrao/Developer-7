package com.atlasgrid.geoops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GeoOps application entry point.
 *
 * <p>@SpringBootApplication combines configuration, component scanning and
 * auto-configuration. SpringApplication.run(...) creates and starts the
 * Spring application context and the embedded web server.</p>
 */
@SpringBootApplication
public class GeoOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeoOpsApplication.class, args);
    }
}
