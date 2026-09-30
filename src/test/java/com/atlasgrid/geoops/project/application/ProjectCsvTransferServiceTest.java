package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectCsvTransferServiceTest {

    @Autowired
    private ProjectCsvTransferService transferService;

    @Autowired
    private ProjectService projectService;

    @Test
    void importsQuotedCsvThroughExistingProjectWorkflow() {
        String csv = """
                projectCode,name,coordinateReferenceSystem
                TX-AUS-050,"Austin, Survey Import",EPSG:4326
                TX-DAL-050,Dallas Survey Import,EPSG:3857
                """;

        ProjectImportResult result = transferService.importCsv(
                new ByteArrayInputStream(
                        csv.getBytes(StandardCharsets.UTF_8)
                )
        );

        assertThat(result.importedCount()).isEqualTo(2);
        assertThat(result.projectCodes())
                .containsExactly("TX-AUS-050", "TX-DAL-050");

        List<GeoProject> projects = projectService.findAll();

        assertThat(projects)
                .extracting(GeoProject::name)
                .containsExactly(
                        "Austin, Survey Import",
                        "Dallas Survey Import"
                );
    }

    @Test
    void exportEscapesCsvValuesThatContainCommas() {
        transferService.importCsv(
                new ByteArrayInputStream(
                        """
                        projectCode,name,coordinateReferenceSystem
                        TX-AUS-051,"Austin, Export Survey",EPSG:4326
                        """.getBytes(StandardCharsets.UTF_8)
                )
        );

        String csv = transferService.exportCsv();

        assertThat(csv)
                .startsWith(ProjectCsvTransferService.HEADER)
                .contains(
                        "TX-AUS-051,"Austin, Export Survey",EPSG:4326"
                );
    }
}
