# Sprint 009 — Multithreading Feature and Lock Evolution

**Sprint length:** 2 weeks  
**Goal:** Turn the existing GeoOps concurrency mechanisms into a clearly evidenced feature, document synchronization risks, add executor/concurrency observability, and evolve the review-state lock to ReentrantLock.

## Story GEO-71 — Prove the multithreaded feature

**Outcome:** Bulk GIS request validation is documented and regression-tested as the concrete multithreaded feature.

Evidence:
- docs/sets/SET-071-MULTITHREADED-FEATURE.md
- ParallelValidationFeatureIntegrationTest.java
- ParallelProjectValidationService.java
- ProjectValidationExecutorConfiguration.java

## Story GEO-72 — Document synchronization risk and lock ordering

**Outcome:** The project records the duplicate-intake race risk, narrow critical section, and current single-JVM lock ordering.

Evidence:
- docs/sets/SET-072-SYNCHRONIZATION-ISSUES.md
- docs/incidents/INC-002-CONCURRENT-DUPLICATE-INTAKE-RACE.md
- ConcurrentProjectIntakeIntegrationTest.java

## Story GEO-73 — Observe executor usage

**Outcome:** GeoOps captures internal validation-executor metrics including active workers, queued tasks, queue capacity and completed/submitted counts.

Evidence:
- docs/sets/SET-073-USING-MULTITHREADING.md
- ProjectValidationExecutorSnapshot.java
- ProjectValidationExecutorMonitor.java
- ProjectValidationExecutorMonitorTest.java

## Story GEO-74 — Capture the concurrency environment

**Outcome:** One internal diagnostic snapshot combines JVM thread evidence with application executor evidence.

Evidence:
- docs/sets/SET-074-CONCURRENCY-ENVIRONMENT.md
- ConcurrencyEnvironmentSnapshot.java
- ConcurrencyEnvironmentDiagnosticsService.java
- ConcurrencyEnvironmentDiagnosticsServiceTest.java

## Story GEO-75 — Migrate review-state locking to ReentrantLock

**Outcome:** ProjectReviewQueue uses explicit ReentrantLock lock/unlock semantics for compound queue/claimed-map state transitions.

Evidence:
- docs/sets/SET-075-REENTRANTLOCK.md
- ProjectReviewQueue.java
- ProjectReviewQueueReentrantLockTest.java

## Sprint review

1. Run the 100-request parallel validation feature test.
2. Re-run the concurrent duplicate-intake race regression.
3. Inspect executor metrics while workers are active and work is queued.
4. Capture the combined concurrency environment snapshot.
5. Demonstrate review queue behavior after the ReentrantLock migration.
6. Run `mvn clean verify` and keep SpotBugs green.
