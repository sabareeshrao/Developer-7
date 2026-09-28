package com.atlasgrid.geoops.project.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectValidationStandardsTest {

    @Test
    void validatesProjectCodesWithoutCreatingAUtilityObject() {
        assertThat(ProjectValidationStandards.isValidProjectCode("TX-AUS-001"))
                .isTrue();
        assertThat(ProjectValidationStandards.isValidProjectCode("bad-code"))
                .isFalse();
    }

    @Test
    void normalizesAndValidatesCrsIdentifiersStatically() {
        assertThat(ProjectValidationStandards.normalizeCrsIdentifier(" epsg:4326 "))
                .isEqualTo("EPSG:4326");

        assertThat(ProjectValidationStandards.isValidCrsIdentifier(" epsg:4326 "))
                .isTrue();
        assertThat(ProjectValidationStandards.isValidCrsIdentifier("WGS84"))
                .isFalse();
    }
}
