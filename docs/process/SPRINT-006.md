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


## Story GEO-58 — Add custom snapshot-field metadata

**Outcome:** The snapshot schema carries explicit runtime metadata that the existing Reflection diagnostics can inspect.

Acceptance criteria:
- Create a custom Java annotation with `@interface`.
- Restrict it to record components with `@Target`.
- Retain it at runtime.
- Annotate the `ProjectSnapshotEntry` record components.
- Read the metadata through `SnapshotReflectionInspector`.
- Add tests for meta-annotations and runtime inspection.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-058-CUSTOM-ANNOTATION.md
- SnapshotField.java
- SnapshotFieldMetadata.java
- ProjectSnapshotEntry.java
- SnapshotReflectionInspector.java
- SnapshotFieldAnnotationTest.java


## Story GEO-59 — Capture JVM memory evidence

**Outcome:** GeoOps can capture lightweight heap/non-heap evidence to support a repeatable memory-leak investigation.

Acceptance criteria:
- Use standard JDK `MemoryMXBean`.
- Capture heap used/committed/max values.
- Capture non-heap used/committed values.
- Record pending-finalization count.
- Do not expose a new public diagnostics endpoint.
- Document that one high-memory sample does not prove a leak.
- Add tests for sane JVM memory values.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-059-FIND-MEMORY-LEAK.md
- docs/operations/JVM-MEMORY-LEAK-DETECTION.md
- JvmMemorySnapshot.java
- JvmMemoryDiagnosticsService.java
- JvmMemoryDiagnosticsServiceTest.java
