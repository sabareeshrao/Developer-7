# Set 50 — GeoOps Project Data Import and Export

**Status:** 50/387+  
**Anchor:** ⭐ How are you importing and exporting data? Can you tell me the technical part of that?

## GeoOps implementation established by this Set

GeoOps now supports a real CSV project-metadata transfer flow.

### Import

~~~text
POST /api/projects/imports/csv
        ↓
Spring MVC MultipartFile
        ↓
ProjectDataTransferController
        ↓
InputStream
        ↓
ProjectCsvTransferService
        ↓
BufferedReader + UTF-8
        ↓
CSV header/row parsing
        ↓
CreateProjectRequest
        ↓
ProjectService.create(...)
        ↓
existing validation
        ↓
duplicate check
        ↓
ProjectCatalog
        ↓
ProjectReviewQueue
~~~

### Export

~~~text
GET /api/projects/exports/csv
        ↓
ProjectCsvTransferService.exportCsv()
        ↓
current ProjectService.findAll()
        ↓
CSV header + escaped values
        ↓
text/csv response
        ↓
Content-Disposition: attachment
        ↓
geoops-projects.csv
~~~

## CSV exchange contract

Import/export fields:

~~~text
projectCode,name,coordinateReferenceSystem
~~~

Example:

~~~csv
projectCode,name,coordinateReferenceSystem
TX-AUS-050,"Austin, Survey Import",EPSG:4326
TX-DAL-050,Dallas Survey Import,EPSG:3857
~~~

Technical rules:
- UTF-8 is used.
- The header is required.
- Empty lines are skipped.
- Quoted values may contain commas.
- Quotes inside quoted values are escaped by doubling them.
- The current line-oriented parser does not support multiline field values.
- Every imported project still uses the existing GeoOps business-validation path.
- Native Java object serialization is not used for this CSV exchange format.

## Upload limits

GeoOps currently bounds synchronous multipart imports:

~~~yaml
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 6MB
~~~

This is intentional. The current endpoint is for modest metadata batches, not very large long-running imports.

---

## Part A

### ✅ 1. While designing a File Handling module, how would you decide which Exceptions should be Checked and which should be Unchecked?

Already covered earlier.

Set 50 applies that design by keeping low-level I/O details inside the data-transfer boundary.

`IOException` is caught at the transfer boundary and wrapped as `ProjectDataTransferException`, which is a GeoOps application exception.

Malformed CSV content is also represented as a project data-transfer failure rather than leaking low-level parsing details into the controller.

### 2. What is the difference between Blocking and Non-Blocking I/O in Spring Boot?

Blocking I/O keeps the processing thread occupied while the I/O operation completes.

The current GeoOps CSV import is deliberately blocking:

~~~text
HTTP request thread
→ read uploaded CSV
→ create projects
→ return import result
~~~

That is acceptable because the endpoint is bounded to modest files.

Non-blocking/reactive I/O is useful when a workload needs to avoid tying up request threads during long I/O waits, but simply changing an API to non-blocking does not automatically make CPU-heavy or long batch processing safe.

For very large imports, GeoOps would change the processing architecture rather than merely changing the API syntax.

### 3. Your REST API allows file uploads, but users complain about large files failing silently. How would you fix this issue?

First, make upload limits and failures explicit.

GeoOps now configures explicit multipart size limits instead of relying on hidden defaults.

For a production-style design I would also:
- return a clear 4xx response for an oversized upload;
- log the rejection with safe operational context;
- expose documented maximum file size;
- validate file type/header early;
- monitor request duration and upload failures.

For truly large files, the better answer is not to keep increasing the synchronous HTTP timeout indefinitely.

### 4. How does Spring MVC handle file uploads, and what configurations are required?

Spring MVC exposes multipart uploads through `MultipartFile`.

GeoOps uses:

~~~java
@PostMapping(
        path = "/imports/csv",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ProjectImportResult importCsv(
        @RequestPart("file") MultipartFile file
)
~~~

The controller obtains the uploaded file's `InputStream` and delegates parsing/business processing to `ProjectCsvTransferService`.

Multipart limits are configured through `spring.servlet.multipart.*`.

### 5. You need to expose a REST API that accepts a CSV file upload and processes 50,000 rows, but the HTTP request times out. How would you redesign the flow?

I would not keep the 50,000-row processing inside one synchronous request.

A scalable redesign is:

~~~text
client uploads/stages file
        ↓
API validates basic metadata
        ↓
create import job
        ↓
return 202 Accepted + job id
        ↓
background/batch worker processes rows
        ↓
persist progress/results
        ↓
client checks job status
~~~

Set 50 does **not** claim GeoOps already has that asynchronous batch infrastructure.

The current implementation is intentionally a small synchronous import. The 50,000-row design is documented as the next architecture shape if the workload requires it.

### 6. Have you heard about Java serialization?

Yes, but GeoOps does not use native Java object serialization for this external import/export contract.

Native Java serialization is about converting Serializable Java object graphs into a Java-specific serialized form.

Set 50 uses a documented CSV exchange format instead:

~~~text
external CSV
→ parse fields
→ CreateProjectRequest
→ application/service logic
~~~

That keeps the transfer contract human-readable and independent from Java class serialization details.

### 💡 7. How do you make a CSV import/export contract safe and predictable?

This question is synthetic because the source bank has the broader import/export and file-processing questions but no focused CSV-contract question.

GeoOps establishes:
- explicit UTF-8 encoding;
- an exact required header;
- a fixed three-column exchange schema;
- CSV quote escaping;
- rejection of malformed rows;
- file-size limits;
- try-with-resources for the upload stream;
- reuse of existing validation and duplicate rules;
- integration tests covering import and export together.

The key rule is that file parsing should not bypass business validation.

---

## Error path

Malformed transfer content throws:

~~~text
ProjectDataTransferException
        ↓
GeoOpsProjectException
        ↓
GeoOpsExceptionHandler
        ↓
HTTP 400 + stable PROJECT_DATA_TRANSFER_FAILED code
~~~

Unexpected internal failures remain handled by the existing generic HTTP 500 path.

## Existing file-processing continuity

Set 50 extends earlier GeoOps file work rather than replacing it.

Already established:
- `DatasetPreflightValidator` recognizes .csv, .json and .geojson file types;
- `ProjectManifestFileExporter` demonstrates UTF-8 file output with BufferedWriter and try-with-resources.

New in Set 50:
- HTTP multipart CSV project import;
- CSV catalog download;
- explicit synchronous upload limits;
- CSV parsing/escaping contract;
- import/export integration tests.

## Executable evidence

### Service-level test

`ProjectCsvTransferServiceTest` verifies:
- quoted CSV names containing commas are imported correctly;
- imported rows become real GeoOps projects;
- CSV export quotes values containing commas.

### REST integration test

`ProjectDataTransferIntegrationTest` verifies:

~~~text
multipart CSV upload
→ HTTP 201
→ importedCount = 2

then

GET CSV export
→ HTTP 200
→ text/csv
→ attachment filename
→ imported rows present
~~~

## Set 50 world decision

- GeoOps project metadata can be imported through a synchronous CSV multipart endpoint.
- GeoOps project metadata can be exported as a downloadable CSV file.
- The exchange schema is projectCode, name, coordinateReferenceSystem.
- Imports use UTF-8 and BufferedReader.
- Imported projects go through the existing ProjectService business path.
- CSV values are escaped on export.
- Multipart uploads are currently capped at 5MB with a 6MB request limit.
- The synchronous endpoint is for modest project-metadata files.
- Large long-running imports should become asynchronous import jobs rather than extended HTTP requests.
- Native Java serialization is not used for the CSV exchange contract.
- Five new master technical questions are covered.
- One previously completed master question is reused with ✅.
- One synthetic technical question is added with 💡.

---

## Experience Answer

In GeoOps, I implemented project-metadata import and export through CSV. For import, the REST API accepts a multipart CSV file, opens its input stream, reads it as UTF-8 with a buffered reader, validates the expected header, parses each row into a `CreateProjectRequest`, and then delegates to the existing `ProjectService.create()` path. That means imported projects still go through the same validation, duplicate checks, catalog insertion, and quality-review queue as normal API-created projects.

For export, we read the current project catalog and generate a UTF-8 CSV response with proper escaping and a `Content-Disposition` attachment header. The current upload is intentionally bounded and synchronous for modest metadata batches. For something like 50,000 rows, I would redesign it as an asynchronous import job with a job ID and status tracking instead of holding one HTTP request open.
