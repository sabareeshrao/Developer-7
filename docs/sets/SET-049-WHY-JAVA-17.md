# Set 49 — Why GeoOps Uses Java 17 Without Heavy Java-17-Specific Feature Usage

**Status:** 49/387+  
**Anchor:** ⭐ Why did you choose Java 17 if you are not using many Java 17-specific features?

## GeoOps answer established by the repository

GeoOps uses Java 17 because the project baseline is driven by **framework compatibility, supportability, and toolchain consistency**, not by a requirement to use every language feature introduced in Java 17.

The repository currently uses Spring Boot 3.3.5.

The Spring Boot 3.3 system requirements state that Spring Boot 3.3 requires at least Java 17.

Java 17 is also an LTS release.

So the project-level reasoning is:

~~~text
Spring Boot 3.x baseline
        ↓
Java 17 minimum compatibility level

Java 17 LTS
        ↓
stable enterprise runtime baseline

same JDK in local build + Maven + CI
        ↓
less version drift

modern Java features available
        ↓
adopt only when they improve the design
~~~

GeoOps already uses one modern Java construct—a `record` for `GeoProject`—but does not add sealed classes or other features merely to prove Java 17 usage.

## Repository evidence

| Decision evidence | File |
|---|---|
| Spring Boot version 3.3.5 | `pom.xml` |
| Java 17 compiler/runtime baseline | `pom.xml` |
| Java 17 local version marker | `.java-version` |
| Java 17 CI | `.github/workflows/ci.yml` |
| Java 17 enforcement | Maven Enforcer in `pom.xml` |
| Runtime proof | `Java17BaselineTest` |
| Existing modern-language use | `GeoProject` record |
| Version rationale | `docs/architecture/ADR-JAVA-17-BASELINE.md` |

## External verification used for the compatibility/support facts

Spring Boot 3.3 system requirements:

https://docs.spring.io/spring-boot/3.3/system-requirements.html

Oracle Java 17 LTS announcement:

https://www.oracle.com/news/announcement/oracle-releases-java-17-2021-09-14/

---

## Part A

### ✅ 1. Why was Java 8 introduced? Why was there a requirement to upgrade from Java 7 to Java 8?

Already covered in Set 45.

The Set 49 connection is that Java-version upgrades are not justified by one syntax feature alone. A release can improve the overall language/platform baseline, APIs, runtime, libraries, tooling and ecosystem compatibility.

That same distinction matters when explaining Java 17.

### 2. Can you tell me some new features of Java 11 which are not there in older versions?

Java 11 continued the platform evolution between Java 8 and Java 17.

Examples include:
- the standardized HTTP Client API;
- running a single-file source program directly;
- local-variable syntax in lambda parameters;
- useful String additions such as `isBlank()`, `lines()` and `repeat()`;
- convenient file-reading/writing APIs such as `Files.readString()` and `Files.writeString()`.

GeoOps does not claim that these Java 11 features drove the project version decision.

This question is useful because it shows that Java 17 sits on top of several generations of platform evolution rather than representing one isolated set of Java-17-only features.

### ✅ 3. Can you tell me some new features that were introduced in Java 17?

Already covered in Set 46.

The important Set 49 point is that using Java 17 does not require the application to heavily use every Java-17-specific feature.

The runtime baseline and the feature-level design decisions are separate.

### ✅ 4. Are you aware of recent Java updates such as Records and Sealed Classes?

Already covered in Set 46.

GeoOps uses a `record` for `GeoProject`.

A sealed hierarchy is still not added because there is no established domain requirement for one.

That is deliberate: use a feature where it improves the model, not just because the JDK supports it.

### 5. Is Spring Boot 3.x compatible with Java 8?

No.

For the Spring Boot 3.3 line used by GeoOps, the official system requirements require at least Java 17.

GeoOps currently declares:

~~~xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>
</parent>
~~~

Therefore the Java 17 baseline is directly aligned with the project's Spring Boot generation.

This is a stronger reason than saying, "we picked Java 17 because we wanted to use a record."

### 💡 6. What is an LTS Java release, and why does it matter when choosing an enterprise project baseline?

This question is synthetic because the 2,308-question bank does not contain an LTS-focused Java-version question.

LTS means **Long-Term Support**.

For a project baseline, the practical value is having a release line intended for longer-lived maintenance/support compared with short-cadence feature releases.

Java 17 is an LTS release.

That makes it a reasonable conservative enterprise baseline even when the codebase is not aggressively adopting every feature introduced in that version.

### 💡 7. If a project does not use many Java-17-specific language features, what benefits still come from choosing Java 17 as the baseline?

This question is synthetic because the source bank asks the experience question but does not contain a separate theory question for this distinction.

The benefits can exist at the **platform level**:

- framework compatibility;
- consistent local/CI/runtime toolchain;
- LTS baseline;
- access to newer Java APIs and runtime improvements;
- ability to adopt newer language features incrementally;
- avoiding a forced dependency on an older JDK merely because most business code uses familiar syntax.

In GeoOps, the decision looks like:

~~~text
Choose Java 17 as platform baseline
        ↓
enforce it consistently
        ↓
use modern features only when useful

record → yes, useful data model

sealed hierarchy → no, no current domain need
~~~

## Why no Java migration story is claimed

The repository does not currently establish that GeoOps was migrated from Java 8 or Java 11.

The master bank contains later migration-specific job-experience questions.

Those should get their own Sets if and when the learning sequence reaches them.

Set 49 therefore says:

~~~text
current project baseline = Java 17
reason = Spring Boot 3 compatibility + LTS + consistent tooling

NOT

"we migrated from Java 8 and solved X/Y/Z"
~~~

because the latter is not yet established by the codebase/world canon.

## Why no new production code is added

The Java 17 runtime contract was already implemented in Set 46.

Set 49 is an **architecture-decision/rationale Set**.

The correct repository change is to strengthen the Java 17 ADR, not to create another endpoint or feature that exists only to justify an interview answer.

## Set 49 world decision

- Java 17 is selected as a project/platform baseline, not merely for Java-17-specific syntax.
- GeoOps uses Spring Boot 3.3.5.
- Spring Boot 3.3 requires Java 17 or newer.
- Java 17 is treated as the project's LTS baseline.
- Local development, Maven and CI remain on the same Java 17 contract.
- GeoOps can use modern language features incrementally.
- Existing record usage is legitimate evidence; sealed classes remain a deliberate non-use.
- No Java 8→17 migration history is claimed.
- Two new master technical questions are covered.
- Three previously completed master questions are reused with ✅.
- Two synthetic technical questions are added with 💡.

---

## Experience Answer

We chose **Java 17** for GeoOps mainly as a project and platform baseline, not because we needed to use every Java 17 language feature. Our application is on Spring Boot 3.3.x, and that Spring Boot generation requires Java 17 or newer. Java 17 is also an LTS release, so it gives us a stable baseline for development, builds, CI, and runtime.

I separate the JDK-version decision from the feature-level coding decision. We already use a record where it fits the `GeoProject` data model, but I would not introduce sealed classes or rewrite working code just to say we are using Java 17 features. The main value is having a supported, consistent modern platform and then adopting individual features only where they improve the design.
