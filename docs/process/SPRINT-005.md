# Sprint 005 — Data Exchange Foundation

**Sprint length:** 2 weeks  
**Goal:** Extend GeoOps from API-only project intake into explicit file-based project data exchange while preserving the existing validation and workflow rules.

## Story GEO-50 — Import and export project metadata through CSV

**Outcome:** Operations can submit a bounded CSV file of project metadata and download the current GeoOps project catalog as CSV without bypassing existing business logic.

Acceptance criteria:
- Add a multipart CSV import endpoint.
- Require the header `projectCode,name,coordinateReferenceSystem`.
- Read imports as UTF-8 with a buffered reader.
- Support normal CSV quoting for commas and doubled quotes.
- Convert each imported row into `CreateProjectRequest`.
- Delegate every imported row to `ProjectService.create(...)`.
- Preserve existing validation, duplicate detection, catalog insertion, and review-queue behavior.
- Add a CSV export endpoint with `text/csv` content type.
- Add a `Content-Disposition` attachment filename.
- Escape exported CSV values correctly.
- Add explicit multipart size limits for the synchronous import path.
- Document that very large imports should be redesigned as asynchronous jobs rather than long-running HTTP requests.
- Do not use native Java object serialization for the external CSV contract.
- Add service and REST integration tests.
- Keep CI green.

Evidence:
- docs/sets/SET-050-DATA-IMPORT-EXPORT.md
- ProjectCsvTransferService.java
- ProjectDataTransferController.java
- ProjectImportResult.java
- ProjectDataTransferException.java
- ProjectCsvTransferServiceTest.java
- ProjectDataTransferIntegrationTest.java
- application.yml
- GeoOpsErrorCode.java

## Sprint Review demo

1. Run `mvn clean verify`.
2. Upload a CSV file to `POST /api/projects/imports/csv`.
3. Confirm the response reports imported project codes.
4. Verify the imported projects use the normal GeoOps catalog/review workflow.
5. Download `GET /api/projects/exports/csv`.
6. Verify the response is `text/csv` with an attachment filename.
7. Show the configured multipart size limit and explain the large-batch redesign boundary.

## Architecture boundary

Set 50 introduces a bounded synchronous transfer path only.

It does not yet introduce:
- database-backed bulk loading,
- asynchronous import-job persistence,
- SFTP,
- object storage,
- Kafka,
- Spring Batch,
- native Java object serialization,
- transactional database rollback.

Those capabilities require their own future anchors.
