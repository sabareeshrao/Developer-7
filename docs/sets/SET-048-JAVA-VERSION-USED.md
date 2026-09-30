# Set 48 — Java Version Used in GeoOps

**Status:** 48/387+  
**Anchor:** ⭐ What's the Java version you are using?

## GeoOps answer established by the repository

The Java version used in GeoOps is **Java 17**.

This Set intentionally reuses the same project evidence established in Sets 46 and 47.

~~~text
.java-version
→ 17

pom.xml
→ java.version = 17
→ maven.compiler.release = 17
→ Maven Enforcer requires [17,18)

GitHub Actions
→ Temurin Java 17

Java17BaselineTest
→ Runtime.version().feature() == 17
~~~

The answer is therefore consistent across local development, compilation, CI, and the tested runtime.

## Repository evidence

| Evidence | File |
|---|---|
| Project JDK marker | `.java-version` |
| Maven Java version | `pom.xml` |
| Maven compiler release | `pom.xml` |
| Enforced runtime range | Maven Enforcer in `pom.xml` |
| CI JDK | `.github/workflows/ci.yml` |
| Runtime verification | `Java17BaselineTest` |
| Modern Java type | `GeoProject` record |
| Version decision | `docs/architecture/ADR-JAVA-17-BASELINE.md` |

---

## Part A

### ✅ 1. Can a machine have multiple versions of JDK or JRE installed?

Already covered.

A machine may have several JDKs installed, but GeoOps must use the Java 17 toolchain selected for this project.

### ✅ 2. Can you tell me the difference between JDK, JRE and JVM?

Already covered.

For GeoOps, the Java 17 JDK is used for development/build work and the JVM executes the compiled application.

### ✅ 3. Can you tell me some new features that were introduced in Java 17?

Already covered in Set 46.

GeoOps is on Java 17 and currently uses a `record` as modern-Java evidence. The project does not claim to use every Java 17 feature.

### ✅ 4. Are you aware of recent Java updates such as Records and Sealed Classes?

Already covered in Set 46.

GeoOps uses `GeoProject` as a record. A sealed hierarchy is still not introduced because there is no established domain requirement for one.

---

## Consistency check

The same answer appears in every technical surface that matters:

~~~text
Developer setup
        ↓
.java-version = 17

Build configuration
        ↓
Maven = Java 17

Build protection
        ↓
Maven Enforcer = only Java 17

CI
        ↓
Temurin 17

Runtime test
        ↓
Runtime.version().feature() == 17
~~~

That means the interview answer is backed by the repository rather than by a disconnected claim.

## Why Set 48 adds no new implementation

Set 48's anchor is another source-bank formulation of the current Java-version question.

The codebase already has:
- the Java 17 version marker;
- compiler release enforcement;
- CI configuration;
- runtime regression proof;
- modern-language evidence.

Adding another Java-version class or test would duplicate established behavior.

The next source anchor asks **why Java 17 was chosen**, which is a different question and should get its own Set.

## Set 48 world decision

- The Java version used by GeoOps is Java 17.
- No Java-version change occurred.
- The Set 46 Java 17 baseline remains authoritative.
- All four supporting technical questions are reused with ✅.
- No new master technical question is added.
- No synthetic technical question is added.
- No production code change is required.
- Unique master technical-question coverage remains 203.
- Synthetic technical-question coverage remains 10.
- The Java-17-choice rationale remains reserved for the next anchor.

---

## Experience Answer

The Java version we use in GeoOps is **Java 17**. We keep that version consistent across the project: Maven compiles for Java 17, the repository's `.java-version` file is set to 17, GitHub Actions uses Temurin 17, and Maven Enforcer prevents the project from being built with a different Java runtime.

So Java 17 is not just what I select in the IDE; it is the enforced Java version for the build and test pipeline as well.
