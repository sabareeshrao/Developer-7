# GeoOps Definition of Done

A GeoOps story is Done only when all applicable checks below are satisfied.

## Code
- Implementation matches the accepted story scope.
- Code follows established package and architecture boundaries.
- No new technology is introduced without a justified project need.
- Error paths are handled intentionally.

## Tests
- New logic has appropriate automated tests.
- Existing tests pass.
- GitHub Actions build is green.

## Review
- Pull request explains what changed and why.
- Reviewer feedback is resolved.
- Risky behavior or design tradeoffs are documented.

## API / Data
When the story changes an API or data contract:
- request/response behavior is documented,
- validation rules are explicit,
- backward compatibility impact is considered.

## GIS-specific checks
When the story handles geospatial input:
- coordinate reference system assumptions are explicit,
- supported file/data formats are documented,
- invalid or incomplete metadata behavior is defined,
- sample data or reproducible validation steps exist.

## Documentation
- Relevant set evidence document is updated.
- world/CANON.md is updated for new permanent world facts.
- state/LEARNING_TRACKER.md marks the covered questions.
- AI_CONTEXT.md reflects material architecture changes.

## Delivery
- Story acceptance criteria are satisfied.
- The change is demonstrable in Sprint Review.
