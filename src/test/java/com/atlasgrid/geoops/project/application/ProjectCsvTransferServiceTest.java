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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                TX-AUS-050,"Austin, Survey Import",epsg:4326
                TX-DAL-050,Dallas Survey Import,EPSG:3857
                """;

        ProjectImportResult result = importCsv(csv);

        assertThat(result.importedCount()).isEqualTo(2);
        assertThat(result.projectCodes())
                .containsExactly("TX-AUS-050", "TX-DAL-050");

        List<GeoProject> projects = projectService.findAll();

        assertThat(projects)
                .extracting(GeoProject::coordinateReferenceSystem)
                .containsExactly("EPSG:4326", "EPSG:3857");
    }

    @Test
    void invalidLaterRowDoesNotPartiallyImportEarlierRows() {
        String csv = """
                projectCode,name,coordinateReferenceSystem
                TX-AUS-505,Valid Project,EPSG:4326
                TX-DAL-505,   ,EPSG:3857
                """;

        assertThatThrownBy(() -> importCsv(csv))
                .isInstanceOf(InvalidProjectRequestException.class);

        assertThat(projectService.findAll()).isEmpty();
    }

    @Test
    void supportsMultilineQuotedValuesAndRoundTripsThem() {
        String csv = """
                projectCode,name,coordinateReferenceSystem
                TX-AUS-506,"Austin
                Survey",EPSG:4326
                """;

        importCsv(csv);
        String exported = transferService.exportCsv();

        ProjectService freshService = ProjectServiceTestFactory.create();
        ProjectCsvTransferService freshTransfer =
                new ProjectCsvTransferService(freshService);

        freshTransfer.importCsv(new ByteArrayInputStream(
                exported.getBytes(StandardCharsets.UTF_8)
        ));

        assertThat(freshService.findAll())
                .singleElement()
                .extracting(GeoProject::name)
                .isEqualTo("Austin\nSurvey");
    }

    @Test
    void neutralizesSpreadsheetFormulaCellsAndRestoresOnImport() {
        String csv = """
                projectCode,name,coordinateReferenceSystem
                TX-AUS-507,"=HYPERLINK(""https://example.invalid"")",EPSG:4326
                """;

        importCsv(csv);
        String exported = transferService.exportCsv();

        assertThat(exported)
                .contains("'=HYPERLINK");

        ProjectService freshService = ProjectServiceTestFactory.create();
        new ProjectCsvTransferService(freshService)
                .importCsv(new ByteArrayInputStream(
                        exported.getBytes(StandardCharsets.UTF_8)
                ));

        assertThat(freshService.findAll())
                .singleElement()
                .extracting(GeoProject::name)
                .isEqualTo("=HYPERLINK("https://example.invalid")");
    }

    @Test
    void malformedCsvDoesNotMutateCatalog() {
        String csv = """
                projectCode,name,coordinateReferenceSystem
                TX-AUS-508,"Unclosed,EPSG:4326
                """;

        assertThatThrownBy(() -> importCsv(csv))
                .isInstanceOf(ProjectDataTransferException.class);

        assertThat(projectService.findAll()).isEmpty();
    }

    private ProjectImportResult importCsv(String csv) {
        return transferService.importCsv(
                new ByteArrayInputStream(
                        csv.getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}
