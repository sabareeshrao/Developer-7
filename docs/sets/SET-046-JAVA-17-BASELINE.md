# Set 46 — Current Java Version

**Status:** 46/387+  
**Anchor:** ⭐ Which Java version do you use in your current project?

## GeoOps answer established by the repository

GeoOps currently uses **Java 17**.

This is not only a documentation statement. The repository now expresses the Java 17 baseline through:

~~~text
.java-version
        ↓
17

pom.xml
        ↓
java.version = 17
maven.compiler.release = 17
Maven Enforcer = [17,18)

GitHub Actions
        ↓
Temurin Java 17

Java17BaselineTest
        ↓
Runtime.version().feature() == 17
~~~

The project therefore has one consistent local-build/CI/runtime expectation.

## Repository evidence

| Java-version evidence | File |
|---|---|
| Developer-machine version marker | `.java-version` |
| Compiler release | `pom.xml` |
| Wrong-JDK build rejection | `maven-enforcer-plugin` in `pom.xml` |
| CI JDK | `.github/workflows/ci.yml` |
| Runtime regression test | `Java17BaselineTest` |
| Modern Java domain model | `GeoProject` record |
| Architecture decision | `docs/architecture/ADR-JAVA-17-BASELINE.md` |

---

## Part A

### ✅ 1. Can a machine have multiple versions of JDK or JRE installed?

Already covered in Set 1.

The Set 46 connection is that a developer machine may contain several JDKs, but GeoOps must select Java 17 for this project.

That is why the repository uses a version marker plus build enforcement instead of assuming whatever `java` happens to be first on a developer's PATH is correct.

### ✅ 2. Can you tell me the difference between JDK, JRE and JVM?

Already covered in Set 1.

For GeoOps:
- the JDK provides the compiler/build tools used to develop and package the project;
- the JVM executes the compiled application;
- the project baseline requires Java 17 consistently for those activities.

### 3. Can you tell me some new features that were introduced in Java 17?

The important distinction is between features **introduced/finalized specifically in Java 17** and modern Java features that are simply available when using Java 17.

Java 17 finalized **sealed classes/interfaces**, which let a type explicitly restrict which classes may extend or implement it.

A Java 17 codebase also has access to modern features delivered in the releases leading up to 17, including:
- records,
- text blocks,
- switch expressions,
- pattern matching for `instanceof`.

GeoOps currently uses a **record** for `GeoProject`.

It does not currently use a sealed hierarchy, so Set 46 does not add one just for interview coverage.

### 4. Are you aware of recent Java updates such as Records and Sealed Classes?

Yes.

A **record** is a compact data-oriented class form. The compiler supplies the canonical data accessors and standard value-oriented methods such as `equals()`, `hashCode()`, and `toString()`.

GeoOps uses:

~~~java
public record GeoProject(
        UUID id,
        String projectCode,
        String name,
        String coordinateReferenceSystem,
        Instant createdAt
) {
}
~~~

That is appropriate because `GeoProject` is currently a data carrier whose state is fixed after construction.

A **sealed class or interface** restricts which types may extend/implement it.

GeoOps is aware of sealed types but does not yet have a business hierarchy that needs that restriction, so no sealed hierarchy is introduced in this Set.

---

## Build enforcement added in Set 46

Before this Set, GeoOps already declared Java 17 in Maven, `.java-version`, and CI.

Set 46 makes the contract fail fast.

~~~xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.5.0</version>
    ...
    <requireJavaVersion>
        <version>[17,18)</version>
    </requireJavaVersion>
</plugin>
~~~

Meaning:

~~~text
JDK 17
→ build allowed

JDK 16
→ build rejected

JDK 18+
→ build rejected
~~~

The exact constraint matches the current fictional project statement: **we are on Java 17**, not merely "Java 17 or newer."

## Executable Set 46 proof

`Java17BaselineTest` verifies:

~~~java
assertThat(Runtime.version().feature())
        .isEqualTo(17);

assertThat(GeoProject.class.isRecord())
        .isTrue();
~~~

This gives another AI or developer executable evidence of both:
- the runtime baseline,
- one modern Java language construct already used by GeoOps.

## Why Set 46 does not answer the Java-17-choice question yet

The source bank contains a separate future job-experience anchor asking why Java 17 was chosen even though the project may not use many Java-17-specific features.

That rationale belongs to its own Set.

Set 46 establishes only:

~~~text
What Java version?
→ Java 17

How do we prove it?
→ local marker + Maven compiler + Maven Enforcer + CI + test
~~~

## Set 46 world decision

- GeoOps' current Java baseline is Java 17.
- `.java-version` remains 17.
- Maven compiles with release 17.
- Maven Enforcer rejects non-Java-17 build runtimes.
- GitHub Actions installs Temurin 17.
- `Java17BaselineTest` verifies runtime feature version 17.
- `GeoProject` remains a record and serves as modern-Java evidence.
- No sealed hierarchy is introduced artificially.
- Two new master technical questions are covered.
- Two previously completed JDK/JRE/JVM questions are reused with ✅.
- No synthetic technical question is required.

---

## Experience Answer

In the current GeoOps project, we use **Java 17**. The version is defined in Maven, the repository has a `.java-version` file set to 17, and our GitHub Actions pipeline also runs the build with Java 17.

I also enforce the runtime version through Maven so a build fails early if someone uses the wrong JDK. In the codebase we already use modern Java constructs available on that baseline, such as the `GeoProject` record, while keeping the version choice itself separate from whether every Java 17 feature is used.
