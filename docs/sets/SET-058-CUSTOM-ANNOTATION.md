# Set 58 — Custom Annotation in GeoOps

**Status:** 58/387+  
**Anchor:** ⭐ Have you tried creating a custom annotation?

## Part A

- [x] ✅ What is Java Reflection and what runtime metadata can it inspect?
- [x] 💡 How do you create a custom Java annotation using `@interface`?
- [x] 💡 What do `@Target`, `@Retention`, and `@Documented` control?
- [x] 💡 How can a runtime custom annotation be read through Reflection?

## GeoOps implementation

Set 58 introduces:

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.RECORD_COMPONENT)
public @interface SnapshotField {
    String description();
    boolean required() default true;
}
```

The internal snapshot DTO now uses it:

```text
ProjectSnapshotEntry
├── projectId                  @SnapshotField
├── projectCode                @SnapshotField
├── name                       @SnapshotField
├── coordinateReferenceSystem  @SnapshotField
└── createdAtEpochMilli        @SnapshotField
```

The Set-56 Reflection inspector now reads the annotation and produces `SnapshotFieldMetadata`.

## Why the annotation is useful

The annotation is not decorative. It gives the compatibility diagnostics a single runtime source for field descriptions and required-state metadata.

`RetentionPolicy.RUNTIME` is required because the Reflection inspector must see the annotation after compilation.

`ElementType.RECORD_COMPONENT` prevents the annotation from being placed on unrelated classes or methods.

## Evidence

- `SnapshotField.java`
- `SnapshotFieldMetadata.java`
- `ProjectSnapshotEntry.java`
- `SnapshotReflectionInspector.java`
- `SnapshotFieldAnnotationTest.java`

## Experience Answer

Yes. In GeoOps I created a custom annotation called `@SnapshotField` for the trusted internal snapshot schema. It carries runtime metadata such as a field description and whether that snapshot component is required.

I restricted it to record components with `@Target`, retained it at runtime with `@Retention(RUNTIME)`, and then extended our Reflection inspector to read the annotation from `ProjectSnapshotEntry`. That gave the annotation a real consumer instead of adding custom annotation syntax only for demonstration.
