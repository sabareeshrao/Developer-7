# Post-Set-33 Codebase Audit and Maintenance

**Checkpoint:** after Set 33, before Set 34 completion  
**Learning status before Set 34:** 33/387+  
**Purpose:** repository-wide maintenance; controller refactoring does not increment the learning counter.

## Audit conclusion

GeoOps is still one cumulative Spring Boot application built from scratch through the learning sequence. The project remains intentionally in-memory and does not yet introduce database persistence, concurrency infrastructure, security, messaging, Docker, Kubernetes, or other later-stage capabilities.

## Fix 1 — Split overloaded project REST responsibilities

Before maintenance, ProjectController owned commands, catalog queries, and derived report/view endpoints through six injected collaborators.

The API contract has been preserved while responsibilities are now separated:

~~~text
ProjectController
→ POST /api/projects
→ POST /api/projects/validate

ProjectQueryController
→ GET /api/projects
→ GET /snapshot
→ GET /recent
→ GET /exists/{projectCode}
→ GET /by-code/{projectCode}
→ GET /by-position/{intakePosition}

ProjectReportController
→ GET /collection-summary
→ GET /sorting-preview
→ GET /crs-catalog
→ GET /manifest
~~~

No endpoint path changed as part of this refactor.

## Fix 2 — Review retry integrity

The audit found that the old endpoint accepted an arbitrary ProjectReviewTask body:

~~~text
POST /api/review-queue/retry-first
~~~

That allowed callers to inject a task that had never actually been claimed.

Set 34 resolves this with claimed-state tracking:

~~~text
claim-next
→ queued LinkedList task removed
→ task stored in HashMap by projectCode

retry(projectCode)
→ only succeeds if projectCode exists in claimed HashMap
→ remove from claimed HashMap
→ add same task to queue front

complete(projectCode)
→ remove task from claimed HashMap
~~~

New endpoints:

~~~text
POST /api/review-queue/{projectCode}/retry
POST /api/review-queue/{projectCode}/complete
~~~

The unsafe request-body retry endpoint is removed.

## Intentionally deferred findings

These remain real concerns but should not be fixed before their learning anchors:

1. ProjectCatalog and ProjectReviewQueue are mutable singleton Spring components and are not thread-safe.
2. ProjectService.create(...) mutates ProjectCatalog and ProjectReviewQueue in two separate in-memory steps; there is no transactional boundary yet.
3. Persistent storage is not yet present.
4. Authentication/authorization is not yet present.
5. Concurrent collections/locking are intentionally deferred.

These are not ignored defects; they are explicit future learning milestones.

## Verification

- Controller split preserves existing routes.
- Claimed-task retry/complete behavior is covered by unit and MockMvc integration tests.
- Final repaired code passed Maven verification in GitHub Actions before Set 34 state was advanced.

## Safe continuation point

Proceed with Set 34:

~~~text
⭐ Have you worked with HashMap?
~~~

The HashMap use case is the claimed review-task registry introduced to close the retry-integrity issue.
