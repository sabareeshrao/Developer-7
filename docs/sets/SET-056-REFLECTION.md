# Set 56 — Reflection in GeoOps

**Status:** 56/387+  
**Anchor:** ⭐ Have you used reflection somewhere in your project?

## Part A

- [x] ✅ Do you know what ClassLoaders are?
- [x] 💡 What is Java Reflection and what runtime metadata can it inspect?
- [x] 💡 What is the difference between `getFields()` and `getDeclaredFields()`?
- [x] 💡 What risks come with using Reflection to bypass encapsulation?

## GeoOps implementation

The previous snapshot work introduced versioned internal DTOs and class-loading compatibility concerns. Set 56 adds a small reflection-based compatibility inspector:

```text
ProjectSnapshotEntry.class
        ↓
Class / RecordComponent Reflection API
        ↓
record component names + types

ProjectSnapshotDocument.class
        ↓
Class / Field Reflection API
        ↓
declared instance-field names
```

The inspector reads metadata only. It does not call `setAccessible(true)`, mutate private fields, or invoke arbitrary methods.

## Why Reflection is appropriate here

The diagnostic requirement is about **type structure known only at runtime**. Reflection lets GeoOps inspect the snapshot DTO contract without creating duplicate handwritten field lists.

This is kept out of normal project-intake business logic because Reflection is more dynamic and less type-safe than ordinary Java calls.

## Evidence

- `SnapshotReflectionInspector.java`
- `SnapshotTypeReport.java`
- `SnapshotReflectionInspectorTest.java`
- `ProjectSnapshotEntry.java`
- `ProjectSnapshotDocument.java`

## Experience Answer

Yes. In GeoOps I used Reflection in the internal snapshot-compatibility diagnostics. After we introduced Java-native snapshot DTOs, I added a small inspector that reads the runtime structure of `ProjectSnapshotEntry` and `ProjectSnapshotDocument`.

It uses `Class`, `RecordComponent`, and `Field` metadata to report record components and declared instance fields. I deliberately kept it read-only and did not use `setAccessible(true)` or mutate private state, because the goal was schema diagnostics rather than bypassing encapsulation.
