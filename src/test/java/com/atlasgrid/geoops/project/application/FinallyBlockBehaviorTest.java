package com.atlasgrid.geoops.project.application;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regression examples for Java finally semantics.
 *
 * <p>The dangerous patterns intentionally live only in test code. Production
 * GeoOps code must not return or throw from finally blocks because doing so can
 * replace the original result or failure.</p>
 */
class FinallyBlockBehaviorTest {

    @Test
    void returnInsideFinallyOverridesValuePreparedByTry() {
        assertThat(returnWithFinallySideEffect()).isEqualTo("finally-result");
    }

    @Test
    void exceptionThrownFromFinallyMasksOriginalTryException() {
        assertThatThrownBy(this::throwPrimaryThenThrowFromFinally)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("cleanup failure")
                .hasNoCause();
    }

    @Test
    void nestedFinallyBlocksRunFromInnerScopeToOuterScope() {
        List<String> events = nestedFinallyOrder();

        assertThat(events).containsExactly(
                "inner-try",
                "inner-finally",
                "outer-finally"
        );
    }

    private String returnWithFinallySideEffect() {
        try {
            return "try-result";
        } finally {
            return "finally-result";
        }
    }

    private void throwPrimaryThenThrowFromFinally() {
        try {
            throw new IllegalArgumentException("primary failure");
        } finally {
            throw new IllegalStateException("cleanup failure");
        }
    }

    private List<String> nestedFinallyOrder() {
        List<String> events = new ArrayList<>();

        try {
            try {
                events.add("inner-try");
            } finally {
                events.add("inner-finally");
            }
        } finally {
            events.add("outer-finally");
        }

        return events;
    }
}
