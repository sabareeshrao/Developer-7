package com.atlasgrid.geoops.project.application;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArrayListBehaviorTest {

    @Test
    void growsBeyondSmallInitialElementCountWhilePreservingOrder() {
        List<Integer> values = new ArrayList<>();

        for (int value = 0; value < 100; value++) {
            values.add(value);
        }

        assertThat(values)
                .hasSize(100)
                .startsWith(0, 1, 2)
                .endsWith(97, 98, 99);
    }

    @Test
    void removeWithPrimitiveIntUsesIndexOverload() {
        List<Integer> values = new ArrayList<>(List.of(10, 20, 30));

        Integer removed = values.remove(1);

        assertThat(removed).isEqualTo(20);
        assertThat(values).containsExactly(10, 30);
    }

    @Test
    void removeWithIntegerObjectUsesValueOverload() {
        List<Integer> values = new ArrayList<>(List.of(10, 20, 30));

        boolean removed = values.remove(Integer.valueOf(20));

        assertThat(removed).isTrue();
        assertThat(values).containsExactly(10, 30);
    }
}
