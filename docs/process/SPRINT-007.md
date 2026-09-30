# Sprint 007 — JVM Profiling and Concurrent Intake

**Sprint length:** 2 weeks  
**Goal:** Complete the JVM-memory tooling story and establish an executable concurrency model for GeoOps request validation and in-memory intake.

## Story GEO-61 — Reproduce VisualVM investigation workflow

**Outcome:** Developers can run the actual GeoOps JVM with a bounded heap and attach VisualVM for heap, allocation and thread investigation.

Evidence:
- docs/sets/SET-061-VISUALVM.md
- docs/operations/VISUALVM-MEMORY-INVESTIGATION.md
- scripts/run-visualvm-lab.sh

## Story GEO-62 — Record memory-retention RCA

**Outcome:** The fictional project has a precise pre-release retention incident with root cause, evidence, fix and regression prevention.

Evidence:
- docs/sets/SET-062-MEMORY-LEAK-EXPERIENCE.md
- docs/incidents/INC-001-MEMORY-DIAGNOSTIC-HISTORY-RETENTION.md
- MemorySnapshotHistoryTest.java

## Story GEO-63 — Add bounded parallel request validation

**Outcome:** Independent GIS request validations execute on a bounded four-thread worker pool without mutating catalog state.

Evidence:
- docs/sets/SET-063-MULTITHREADED-ENVIRONMENT.md
- ParallelProjectValidationService.java
- ProjectValidationExecutorConfiguration.java
- ProjectBatchValidationController.java
- ParallelProjectValidationServiceConcurrencyTest.java
- ProjectBatchValidationIntegrationTest.java

## Story GEO-64 — Make synchronization placement explicit

**Outcome:** The codebase proves which methods use method-level synchronization, which use narrower synchronized blocks, and which remain non-synchronized.

Evidence:
- docs/sets/SET-064-SYNCHRONIZED-NON-SYNCHRONIZED.md
- ProjectSynchronizationContractTest.java

## Story GEO-65 — Verify concurrent-user intake

**Outcome:** The current single-JVM intake flow remains consistent under simultaneous unique requests and duplicate-code races.

Evidence:
- docs/sets/SET-065-CONCURRENT-USERS.md
- docs/architecture/ADR-CONCURRENT-INMEMORY-INTAKE.md
- ConcurrentProjectIntakeIntegrationTest.java

## Sprint review

1. Start GeoOps with the VisualVM lab script and inspect heap/thread behavior.
2. Review the controlled memory-retention RCA and bounded-history regression.
3. Call the parallel validation endpoint with mixed valid/invalid requests.
4. Run synchronization-contract tests.
5. Run concurrent unique and duplicate intake races.
6. Run `mvn clean verify` and keep SpotBugs green.
