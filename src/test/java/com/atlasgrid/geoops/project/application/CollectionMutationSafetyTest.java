package com.atlasgrid.geoops.project.application;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Controlled regression examples for collection best practices.
 *
 * <p>Unsafe patterns intentionally remain in test code only.</p>
 */
class CollectionMutationSafetyTest {

    @Test
    void removingDirectlyFromArrayListDuringEnhancedForIsFailFast() {
        List<String> projectCodes =
                new ArrayList<>(List.of("TX-AUS-022", "TX-DAL-022", "TX-HOU-022"));

        assertThatThrownBy(() -> {
            for (String projectCode : projectCodes) {
                if (projectCode.equals("TX-AUS-022")) {
                    projectCodes.remove(projectCode);
                }
            }
        }).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    void removeIfSafelyAppliesRemovalThroughCollectionApi() {
        List<String> projectCodes =
                new ArrayList<>(List.of("TX-AUS-022", "TX-DAL-022", "TX-HOU-022"));

        projectCodes.removeIf(code -> code.startsWith("TX-DAL"));

        assertThat(projectCodes)
                .containsExactly("TX-AUS-022", "TX-HOU-022");
    }

    @Test
    void mutatingHashSetElementHashStateBreaksReliableLookup() {
        MutableHashKey key = new MutableHashKey(1);
        Set<MutableHashKey> keys = new HashSet<>();
        keys.add(key);

        key.setValue(2);

        assertThat(keys.contains(key)).isFalse();
    }

    @Test
    void mutatingHashMapKeyHashStateBreaksReliableLookup() {
        MutableHashKey key = new MutableHashKey(1);
        Map<MutableHashKey, String> values = new HashMap<>();
        values.put(key, "GeoOps");

        key.setValue(2);

        assertThat(values.get(key)).isNull();
    }

    private static final class MutableHashKey {
        private int value;

        private MutableHashKey(int value) {
            this.value = value;
        }

        private void setValue(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof MutableHashKey that
                    && value == that.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }
}
