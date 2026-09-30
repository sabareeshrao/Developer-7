# Set 57 — Conditional Annotations in Spring Boot

**Status:** 57/387+  
**Anchor:** ⭐ Have you used Conditional annotations in Spring Boot?

## Part A

- [x] ✅ What is the role of `@SpringBootApplication` annotation in a Spring Boot application?
- [x] ✅ What are the components that make up `@SpringBootApplication` annotation?
- [x] 💡 What does `@ConditionalOnProperty` do in Spring Boot?
- [x] 💡 What is the difference between `havingValue` and `matchIfMissing`?
- [x] 💡 When should optional diagnostic or integration beans be conditional instead of always loaded?

## GeoOps implementation

Set 56 introduced `SnapshotReflectionInspector`. Set 57 prevents that diagnostic capability from becoming part of the normal runtime unless operations explicitly enables it.

```text
geoops.snapshot.diagnostics.enabled=false
        ↓
no SnapshotReflectionInspector bean

geoops.snapshot.diagnostics.enabled=true
        ↓
@ConditionalOnProperty matches
        ↓
SnapshotReflectionInspector bean created
```

Configuration:

```yaml
geoops:
  snapshot:
    diagnostics:
      enabled: false
```

## Why conditional configuration fits

The Reflection inspector is operational diagnostics, not required project-intake functionality. Loading it only when requested keeps the default application context smaller and makes the feature flag explicit.

The condition is tested with `ApplicationContextRunner` for:
- missing property;
- property set to false;
- property set to true.

## Evidence

- `SnapshotDiagnosticsConfiguration.java`
- `SnapshotDiagnosticsConfigurationTest.java`
- `application.yml`
- `SnapshotReflectionInspector.java`

## Experience Answer

Yes. In GeoOps I used Spring Boot conditional configuration for the snapshot Reflection diagnostics. The inspector is useful during compatibility investigation, but it is not required for normal GIS project intake, CSV exchange, or REST processing.

I registered it through a configuration class using `@ConditionalOnProperty`. By default `geoops.snapshot.diagnostics.enabled` is false, so the bean does not exist. When the property is explicitly set to true, Spring creates the Reflection inspector. I also tested the true, false, and missing-property cases with `ApplicationContextRunner`.
