package com.atlasgrid.geoops.project.validation;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A temporary, thread-confined project code for a validation worker.
 * Never use it as authoritative project state.
 */
public final class ValidationTraceContext {

    private static final ThreadLocal<String> PROJECT_CODE =
            new ThreadLocal<>();

    private ValidationTraceContext() {
        throw new IllegalStateException("Utility class");
    }

    public static String currentProjectCode() {
        return PROJECT_CODE.get();
    }

    static <T> T withProjectCode(
            String code, Supplier<T> work
    ) {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(work, "work");
        PROJECT_CODE.set(code);
        try {
            return work.get();
        } finally {
            PROJECT_CODE.remove();
        }
    }
}
