# Sprint 002 — Project Output Foundation

**Sprint length:** 2 weeks  
**Goal:** Begin turning GeoOps project data into useful downstream-readable outputs.

## Committed story

### Story GEO-4 — Generate a plain-text GIS project manifest

**Outcome:** Operations/support users can retrieve a compact text representation of the GIS projects currently known to GeoOps.

Acceptance criteria:
- GeoOps exposes a plain-text project manifest endpoint.
- The manifest includes project count.
- Each project includes ID, project code, name, coordinate reference system and creation time.
- Text assembly avoids repeated immutable String concatenation inside the project loop.
- The mutable text buffer is local to one formatting operation and is not shared across web requests.
- Formatter behavior is unit tested.
- Existing application CI remains green.

Implementation evidence:
- `ProjectManifestFormatter.java`
- `ProjectController.java`
- `ProjectManifestFormatterTest.java`
- Set 4 evidence document

## Sprint Review demo

1. Start GeoOps.
2. Create two GIS project records with `POST /api/projects`.
3. Call `GET /api/projects/manifest`.
4. Verify both records are returned in one text manifest.
5. Run `mvn clean verify`.

## Design note

GeoOps uses `StringBuilder` as a **method-local mutable accumulator** for this output. It does not use one shared `StringBuilder` bean, and it does not use `StringBuffer` because this formatter does not need synchronization around one shared mutable buffer.
