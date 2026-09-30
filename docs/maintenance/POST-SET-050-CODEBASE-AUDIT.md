# Post-Set-050 Codebase Audit and Maintenance

## Scope

This maintenance pass was performed after Set 50. It is **not a learning Set** and does not change question coverage.

Question status remains:

```text
Completed Sets: 50
Completed original anchors: 50
Synthetic ⭐⭐ anchors: 0
Unique master technical questions: 210
Synthetic 💡 technical questions: 13
Status: 50/387+
```

The next learning anchor remains Set 51:

⭐ **What challenge did you face while deserializing data?**

## First fully green maintenance code checkpoint

`7e154b98e9496969b3e5bf5a04748b62367b51aa`

At that checkpoint the complete Maven test suite and SpotBugs gate passed on the maintained codebase.

## Issues fixed

### 1. Partial CSV imports

Before maintenance, CSV rows were committed one at a time. A later invalid row could fail the HTTP request after earlier rows had already changed the catalog and review queue.

Now import is two-phase:

```text
parse entire CSV
→ validate/canonicalize every request
→ reject intra-batch duplicates
→ reject catalog conflicts
→ publish the whole batch
→ enqueue the whole review batch
```

`ProjectService.createAllAtomically(...)` and `ProjectCatalog.addAllAtomically(...)` provide all-or-nothing behavior for the current in-memory implementation.

This is an in-memory atomicity guarantee, not a database transaction claim.

### 2. Validation bypass from CSV import

Bean Validation on `@Valid @RequestBody` protected JSON REST requests but did not protect programmatic callers such as CSV import.

`RequiredProjectFieldsValidationRule` now enforces required project fields below MVC so every intake path receives the same rule.

### 3. ProjectCatalog concurrency

`ProjectCatalog` continues to use the ArrayList/HashSet semantics established by earlier Sets, but it now synchronizes state access and batch publication inside the collection owner.

A concurrency regression test verifies concurrent distinct additions keep list and identity state consistent.

### 4. CRS canonicalization

Accepted CRS identifiers are now canonicalized before storage.

For example:

```text
" epsg:4326 "
→ validate
→ normalize
→ store "EPSG:4326"
```

Delivery selection also normalizes the query CRS.

### 5. Multipart size errors

Oversized uploads now have an explicit HTTP 413 path with error code `REQUEST_TOO_LARGE` instead of falling into the generic HTTP 500 handler.

### 6. CSV parser correctness and round-trip safety

The hand-written parser was replaced with Apache Commons CSV 1.14.1 using RFC 4180 parsing/printing.

The transfer path now covers:
- quoted commas,
- doubled quotes,
- multiline quoted fields,
- optional UTF-8 BOM,
- exact expected header,
- malformed CSV rejection,
- export/import round trips.

### 7. Spreadsheet formula injection

CSV export neutralizes user-controlled cells beginning with formula prefixes `=`, `+`, `-`, or `@` after leading whitespace.

The importer understands the transport escape so GeoOps export→import preserves the logical application value.

### 8. Review queue transition race

Queued→claimed and claimed→retry/complete transitions now use one workflow state lock, removing the interval where a task could temporarily appear in neither state.

### 9. Server I/O vs client-data errors

Malformed CSV remains a client-visible data-transfer error.

Unexpected server-side transfer I/O now uses `PROJECT_DATA_TRANSFER_IO_FAILED` and maps to HTTP 500 without leaking the underlying exception message.

### 10. Dataset root-path NPE

`DatasetFormat.matches(...)` now safely handles paths whose `getFileName()` is null.

### 11. Framework/dependency maintenance

The current project was upgraded from the historical Spring Boot 3.3.5 baseline to **Spring Boot 4.1.1** while keeping the Java 17 baseline.

Historical Set documents remain unchanged where they describe the state that existed when those Sets were completed.

Boot 4 MVC test support is explicitly included with `spring-boot-webmvc-test`, and affected `AutoConfigureMockMvc` imports were migrated.

Apache Commons CSV 1.14.1 is now an explicit project dependency.

### 12. CI hardening

`mvn clean verify` now includes SpotBugs.

Pull requests also run GitHub dependency review.

The final maintenance code passes:
- the full test suite,
- Spring Boot 4.1.1 startup/integration tests,
- SpotBugs.

## Regression tests added

- `ProjectImportAtomicityTest`
- `ProjectCatalogConcurrencyTest`
- `ProjectCrsNormalizationIntegrationTest`
- `GeoOpsUploadLimitHandlerTest`
- expanded `ProjectCsvTransferServiceTest`
- expanded `ProjectDataTransferIntegrationTest`
- expanded `ProjectValidationServiceTest`

Covered failure modes include:
- later invalid CSV row rollback,
- duplicate-in-batch rollback,
- blank-name validation below MVC,
- concurrent catalog writes,
- CRS canonical storage,
- multiline CSV round trip,
- malformed CSV rejection,
- spreadsheet formula-safe export,
- HTTP 413 mapping.

## Current transfer invariant

```text
multipart CSV
→ Commons CSV parse
→ CreateProjectRequest batch
→ application-level validation
→ CRS canonicalization
→ duplicate checks
→ atomic in-memory catalog publication
→ atomic review batch enqueue
```

## Intentionally deferred risks

These are not accidental omissions and should not be introduced until a future anchor naturally requires them:

- authentication/authorization;
- database persistence / database transactions;
- PostgreSQL/PostGIS;
- asynchronous persistent import jobs for very large files;
- Kafka/Redis/cloud object storage;
- Docker/Kubernetes.

The current synchronous upload remains bounded to 5MB per file and 6MB per multipart request.

## Historical-version rule

Sets 1–50 may mention Spring Boot 3.3.5 because that was the project state at the time.

After this maintenance checkpoint, **current code wins**:

```text
Java 17
Spring Boot 4.1.1
Commons CSV 1.14.1
Maven + SpotBugs
GitHub Actions
```

Do not rewrite historical Set evidence solely to make old descriptions look current.
