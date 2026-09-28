# Set 4 — StringBuilder and StringBuffer in GeoOps

**Status:** 4/387+  
**Anchor:** ⭐ Have you worked with StringBuilder and StringBuffer?

## Job-experience answer established by the GeoOps codebase

Yes.

In GeoOps, `StringBuilder` is used in `ProjectManifestFormatter` to assemble a multi-line GIS project manifest containing project IDs, project codes, names, coordinate reference systems and creation timestamps.

The important design decision is that the builder is created **inside the formatting method**:

```java
StringBuilder manifest = new StringBuilder();
```

Each call gets its own mutable buffer, so concurrent HTTP requests do not share the same builder.

We do not use `StringBuffer` for this case because its synchronized methods add coordination that is unnecessary when the mutable object is confined to one request/operation.

The resulting endpoint is:

```text
GET /api/projects/manifest
```

and returns `text/plain`.

## Repository evidence

| Topic | Evidence |
|---|---|
| Practical StringBuilder use | `ProjectManifestFormatter.java` |
| REST exposure | `ProjectController.java` |
| Output behavior tests | `ProjectManifestFormatterTest.java` |
| Agile story | `docs/process/SPRINT-002.md` |
| Prior Java 17 baseline | Set 1 |

---

## Part A

### 1. Can you discuss a scenario where StringBuilder is preferable over StringBuffer? — Master 50

Use `StringBuilder` when one thread or one method owns the mutable text being assembled and synchronization is unnecessary.

GeoOps is a direct example. The manifest formatter creates a fresh builder inside `format(...)`, appends many fields, and converts it to one immutable `String` at the end.

```text
one formatter call
      ↓
new StringBuilder
      ↓
append many project fields
      ↓
toString()
      ↓
response
```

There is no shared builder requiring synchronized access.

### 2. If you want a mutable version of String, what would you use? — Master 51

For most single-threaded/local construction, use `StringBuilder`.

A Java `String` is immutable: operations that conceptually change the text produce a new string value.

`StringBuilder` maintains a mutable character sequence so repeated appends can build one result efficiently.

GeoOps uses it for the project manifest because the output grows line by line.

### 3. Why are you not using StringBuffer? — Master 52

`StringBuffer` is mutable like `StringBuilder`, but its main operations are synchronized.

That can be useful when multiple threads intentionally share the same mutable buffer, but GeoOps avoids that design.

The formatter instead follows:

```text
request A → local StringBuilder A
request B → local StringBuilder B
request C → local StringBuilder C
```

Because the buffers are independent, `StringBuilder` is simpler and avoids unnecessary synchronization overhead.

### 4. What happens internally when you concatenate two String objects using the + operator? — Master 55

Because `String` objects are immutable, concatenation creates a resulting string rather than modifying either original string.

On modern Java versions such as Java 17, the compiler/runtime can optimize `+` concatenation using JVM string-concatenation machinery such as `invokedynamic`.

The practical rule is still important: repeated concatenation inside a loop can create unnecessary intermediate work and makes intent less clear when building a large dynamic result.

GeoOps therefore uses an explicit `StringBuilder` for the multi-record manifest loop.

### 5. A web server handles thousands of requests involving String manipulation. What would you choose among String, StringBuilder and StringBuffer, and which would perform better? — Master 2184

The correct answer depends on ownership and mutation.

- Use `String` for immutable values such as project codes and CRS names.
- Use a **method-local `StringBuilder`** for repeated assembly within one request.
- Use `StringBuffer` only when a design genuinely requires several threads to mutate the same character buffer.

GeoOps does not create a singleton/shared mutable builder. Each manifest request gets a new builder, so request concurrency is handled by isolation instead of synchronized string mutation.

---

## GeoOps implementation

`ProjectManifestFormatter` builds:

```text
GEOOPS PROJECT MANIFEST
projectCount=2
---
id=...
projectCode=TX-AUS-001
name=Austin Survey Intake
crs=EPSG:4326
createdAt=...
---
id=...
projectCode=TX-AUS-002
name=Austin Control Network
crs=EPSG:2277
createdAt=...
```

The controller exposes it as:

```text
GET /api/projects/manifest
Content-Type: text/plain
```

## Why this is a real project use instead of a toy demo

GeoOps already owns project metadata. Operational workflows commonly need lightweight text outputs for inspection, support, diagnostics or later delivery tooling.

Set 4 therefore extends the same project data flow:

```text
Project intake
     ↓
ProjectService
     ↓
List<GeoProject>
     ↓
ProjectManifestFormatter
     ↓
StringBuilder
     ↓
plain-text manifest
```

No database, file-export subsystem or delivery engine is invented yet. Those will be added only when later anchors justify them.

## Set 4 world decision

- GeoOps has a plain-text project manifest endpoint.
- `StringBuilder` is the standard choice for local repeated text assembly.
- Mutable builders must remain operation-local unless a later design explicitly requires shared state.
- `StringBuffer` is understood but is not used for the manifest because synchronization is unnecessary.
