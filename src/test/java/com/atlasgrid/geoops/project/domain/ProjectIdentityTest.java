package com.atlasgrid.geoops.project.domain;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectIdentityTest {

    @Test
    void separateObjectsWithSameProjectCodeAreLogicallyEqual() {
        ProjectIdentity first = new ProjectIdentity("TX-AUS-001");
        ProjectIdentity second = new ProjectIdentity("TX-AUS-001");

        assertThat(first)
                .isNotSameAs(second)
                .isEqualTo(second);

        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void hashSetUsesEqualsAndHashCodeToDetectLogicalDuplicate() {
        Set<ProjectIdentity> identities = new HashSet<>();

        boolean firstAdded = identities.add(new ProjectIdentity("TX-AUS-001"));
        boolean duplicateAdded = identities.add(new ProjectIdentity("TX-AUS-001"));

        assertThat(firstAdded).isTrue();
        assertThat(duplicateAdded).isFalse();
        assertThat(identities).hasSize(1);
    }

    @Test
    void differentProjectCodesAreNotEqual() {
        ProjectIdentity first = new ProjectIdentity("TX-AUS-001");
        ProjectIdentity second = new ProjectIdentity("TX-AUS-002");

        assertThat(first).isNotEqualTo(second);
    }
}
