# Sprint 006 — Runtime Introspection and JVM Diagnostics

**Sprint length:** 2 weeks  
**Goal:** Extend the GeoOps internal snapshot tooling with narrowly scoped runtime introspection and JVM diagnostics without changing the public GIS data contracts.

## Story GEO-56 — Inspect snapshot compatibility metadata with Reflection

**Outcome:** Developers can inspect the structure of the internal snapshot DTOs at runtime without bypassing encapsulation.

Acceptance criteria:
- Use the standard Java Reflection API.
- Inspect record components for `ProjectSnapshotEntry`.
- Inspect declared instance-field names for `ProjectSnapshotDocument`.
- Do not mutate private fields.
- Do not call `setAccessible(true)`.
- Add regression tests.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-056-REFLECTION.md
- SnapshotReflectionInspector.java
- SnapshotTypeReport.java
- SnapshotReflectionInspectorTest.java


## Story GEO-57 — Make snapshot diagnostics conditional

**Outcome:** Reflection diagnostics are optional and disabled unless explicitly enabled by configuration.

Acceptance criteria:
- Use Spring Boot `@ConditionalOnProperty`.
- Create `SnapshotReflectionInspector` only when `geoops.snapshot.diagnostics.enabled=true`.
- Keep the default configuration disabled.
- Verify enabled, disabled and property-absent cases with application-context tests.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-057-CONDITIONAL-ANNOTATIONS.md
- SnapshotDiagnosticsConfiguration.java
- SnapshotDiagnosticsConfigurationTest.java
- application.yml
