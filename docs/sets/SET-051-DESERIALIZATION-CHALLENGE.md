# Set 51 — Deserialization Challenge

**Status:** 51/387+  
**Anchor:** ⭐ What challenge did you face while deserializing data?

## Part A
- [x] ✅ Have you heard about Java serialization?
- [x] Are Serialization and Deserialization simple to implement, or can we face challenges with them?
- [x] A Client sends snake_case JSON fields while your Java Fields use camelCase. How would you map them correctly?
- [x] Can JSON Serialization and Deserialization be customized instead of using the default Jackson behavior?
- [x] How does Spring Boot handle JSON conversion internally?
- [x] What happens when Validation fails for a Request Body Object?

## GeoOps implementation

Set 51 is specifically about the Spring/Jackson JSON request-body boundary:

```text
HTTP JSON
→ Spring MVC
→ Jackson 3
→ CreateProjectRequest
→ Bean Validation
→ ProjectService
→ application/domain validation
```

Known legacy names `project_code` and `coordinate_reference_system` are accepted with `@JsonAlias`, while Java remains camelCase.

Unknown JSON fields are rejected with:

```yaml
spring.jackson.deserialization.fail-on-unknown-properties: true
```

This catches field-name drift and typos instead of silently discarding them.

## Error separation

```text
malformed JSON / unknown property
→ REQUEST_MALFORMED

successful deserialization + blank required field
→ REQUEST_VALIDATION_FAILED

successful deserialization + invalid GeoOps rule
→ PROJECT_VALIDATION_FAILED
```

## Boundary

Set 50 CSV import remains Apache Commons CSV parsing. It is not renamed as native Java deserialization. Set 51 uses Jackson JSON deserialization. Native Java `ObjectInputStream` is not introduced until Set 52.

## Evidence

- `CreateProjectRequest.java`
- `application.yml`
- `GeoOpsExceptionHandler.java`
- `ProjectJsonDeserializationIntegrationTest.java`

## Experience Answer

In GeoOps, one deserialization challenge was handling request-contract differences without making the Java model inconsistent. Our request DTO uses camelCase fields, while a known legacy payload can use names such as `project_code` and `coordinate_reference_system`. I handled those known variations explicitly with Jackson aliases.

I also configured the JSON boundary to reject unknown properties, so a misspelled field becomes a safe `REQUEST_MALFORMED` response instead of being silently ignored. After Jackson creates the request object, Bean Validation and the existing GeoOps domain validation still run as separate stages.
