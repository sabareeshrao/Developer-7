# Set 12 — Enum in GeoOps

**Status:** 12/387+  
**Anchor:** ⭐ Have you worked with Enum in your project?

## Project implementation

Yes. GeoOps now uses a Java enum for the fixed set of inbound dataset formats supported by the standalone preflight process:

```java
public enum DatasetFormat {
    CSV(".csv"),
    JSON(".json"),
    GEOJSON(".geojson");
}
```

Before Set 12, `DatasetPreflightValidator` contained:

```java
Set.of(".csv", ".json", ".geojson")
```

That works, but the values are only Strings.

Now GeoOps has a domain type where each supported format can own both data and behavior:

```text
DatasetFormat
   ├─ CSV     → .csv
   ├─ JSON    → .json
   └─ GEOJSON → .geojson
```

The validator now asks:

```java
DatasetFormat.supports(dataset)
```

instead of manually knowing the supported extensions.

---

## Part A

### 1. Can you explain Enum in Java? — Master 314

An enum is a special Java type used to model a fixed, known set of named values.

GeoOps has exactly this situation in its dataset preflight tool. The supported file formats are intentionally limited to:

```text
CSV
JSON
GEOJSON
```

So instead of representing those domain choices as unrelated strings, GeoOps models them as `DatasetFormat` constants.

Each enum constant is an instance of the enum type.

### 2. What's the purpose of using Enum? — Master 315

The purpose is to represent a closed set of valid choices in a type-safe way.

With raw strings, a developer could accidentally write:

```text
".geo-json"
".JSONFILE"
"geojson"
```

and the compiler cannot tell whether those values are valid domain choices.

With:

```java
DatasetFormat.GEOJSON
```

the valid values are explicit and compiler-checked.

### 3. Can you tell me the advantages of Enum over constants? — Master 313

Compared with unrelated String/int constants, enums provide:

- type safety;
- a clear finite set of choices;
- readable names;
- fields and constructors;
- instance/static methods;
- support for `values()` and `valueOf()`;
- use in `switch`;
- the ability to implement interfaces.

GeoOps specifically benefits because extension text and matching behavior live with the format itself instead of being split across multiple constants and validator code.

### 4. What is an enum in Java, and how is it different from a set of constants? — Master 2724

A plain constants holder might look like:

```java
public static final String CSV = ".csv";
public static final String JSON = ".json";
```

Those are still ordinary String values.

An enum creates a dedicated type:

```java
DatasetFormat format;
```

and that variable can hold only a `DatasetFormat` constant.

The enum can also contain:

```java
private final String extension;
public String extension()
public boolean matches(Path dataset)
public static boolean supports(Path dataset)
```

So it represents both the allowed values and behavior related to those values.

### 5. Let's say you are working in a payment system with different methods like credit card, UPI and net banking. How would you use Enums and what would be your strategy? — Master 316

The same design principle applies.

For a fixed payment-method set, an enum could model:

```text
CREDIT_CARD
UPI
NET_BANKING
```

If each method needs small fixed metadata or behavior, that can live with the enum.

If payment processing becomes large or dependency-heavy, the enum should usually identify the method while separate strategy classes perform the complex processing.

GeoOps follows the same boundary.

`DatasetFormat` owns small format-specific facts and matching behavior. The complete preflight workflow remains in `DatasetPreflightValidator` rather than turning the enum into a large service.

### 6. Can Enums implement interfaces? — Master 318

Yes.

A Java enum can implement one or more interfaces.

That can be useful when enum constants must participate in a common behavior contract.

GeoOps does not currently need an interface for `DatasetFormat`, so Set 12 does not introduce one simply to demonstrate syntax. The existing enum methods are enough for the current requirement.

---

## GeoOps Enum flow

Before:

```text
DatasetPreflightValidator
        ↓
Set<String>
        ↓
".csv" ".json" ".geojson"
```

After:

```text
DatasetPreflightValidator
        ↓
DatasetFormat
        ↓
CSV | JSON | GEOJSON
        ↓
extension + matching behavior
```

## Enum with state

Each enum constant is created with its extension:

```java
CSV(".csv"),
JSON(".json"),
GEOJSON(".geojson");
```

The enum stores:

```java
private final String extension;
```

and exposes:

```java
public String extension()
```

This shows that enums are not merely labels; they can have constructors, fields and methods.

## Enum behavior

`DatasetFormat.matches(...)` determines whether one format matches a particular path.

`DatasetFormat.supports(...)` loops through:

```java
DatasetFormat.values()
```

and returns true when one enum constant matches.

`supportedExtensions()` derives the user-facing supported-format list from the enum instead of duplicating it elsewhere.

## Tests

`DatasetFormatTest` verifies:
- each enum constant owns the expected extension;
- supported extensions are detected case-insensitively;
- unsupported extensions are rejected;
- the supported-extension display is generated from enum values.

`DatasetPreflightValidatorTest` now loops over:

```java
DatasetFormat.values()
```

so every enum-defined format must pass preflight.

## Set 12 world decision

- GeoOps uses `DatasetFormat` for the fixed supported preflight formats.
- Current constants are `CSV`, `JSON`, and `GEOJSON`.
- File-extension knowledge belongs to the enum.
- The validator no longer owns a duplicate raw String set.
- Enum behavior remains small and deterministic; orchestration stays in `DatasetPreflightValidator`.
- No new GIS format is introduced in Set 12; the enum formalizes the three formats already established in Set 2.

---

## Experience Answer

Yes, I have used Enum in the GeoOps project. In our standalone GIS dataset preflight flow, we support a fixed set of input formats: CSV, JSON, and GeoJSON. Initially those supported extensions were stored as raw String values inside the validator, but I replaced that with a `DatasetFormat` enum.

Each enum constant owns its extension, and the enum also provides small helper behavior for checking whether a file matches one of the supported formats. Then `DatasetPreflightValidator` works with `DatasetFormat` instead of maintaining its own String constants. I used Enum there because the supported formats are a closed set of domain values, and the enum gives us type safety, centralized format metadata, and cleaner validation code without moving the overall workflow logic into the enum.
