package com.atlasgrid.geoops.project.application;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreeSetBehaviorTest {

    @Test
    void naturalOrderingTreeSetRejectsNull() {
        TreeSet<String> values = new TreeSet<>();

        assertThatThrownBy(() -> values.add(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void comparatorReturningZeroTreatsValuesAsDuplicateForSetMembership() {
        TreeSet<String> values =
                new TreeSet<>(
                        Comparator.comparingInt(String::length)
                );

        assertThat(values.add("EPSG:1")).isTrue();
        assertThat(values.add("CRS-123")).isFalse();
        assertThat(values).containsExactly("EPSG:1");
    }
}
