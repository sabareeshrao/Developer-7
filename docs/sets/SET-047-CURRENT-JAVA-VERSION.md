# Set 47 — Current Java Version in Day-to-Day Work

**Status:** 47/387+  
**Anchor:** ⭐ Currently, which Java version are you working on?

## GeoOps answer established by the repository

Currently, GeoOps is worked on with **Java 17**.

Set 46 already established the version as an enforceable project baseline. Set 47 confirms that the same version is the current day-to-day development version rather than introducing another Java-version story.

The repository proof is unchanged and intentionally reused:

~~~text
developer version marker
→ .java-version = 17

Maven
→ java.version = 17
→ maven.compiler.release = 17
→ Maven Enforcer requires [17,18)

CI
→ Temurin Java 17

runtime regression
→ Runtime.version().feature() == 17
~~~

## Repository evidence

| Current-version evidence | Repository file |
|---|---|
| Local project version marker | `.java-version` |
| Maven compiler/build version | `pom.xml` |
| Wrong-JDK rejection | Maven Enforcer in `pom.xml` |
| CI development/runtime environment | `.github/workflows/ci.yml` |
| Runtime verification | `Java17BaselineTest` |
| Modern Java model | `GeoProject` record |
| Version architecture decision | `ADR-JAVA-17-BASELINE.md` |

---

## Part A

### ✅ 1. Can a machine have multiple versions of JDK or JRE installed?

Already covered in Sets 1 and 46.

The practical current-project point is that a machine may have multiple JDKs, but the GeoOps project must actively select Java 17.

The repository does not trust an arbitrary default JDK.

### ✅ 2. Can you tell me the difference between JDK, JRE and JVM?

Already covered in Sets 1 and 46.

For the current GeoOps workflow:
- the Java 17 JDK is used to compile, test, and package the service;
- the JVM executes the application;
- Maven and CI both use the same Java 17 baseline.

### ✅ 3. Can you tell me some new features that were introduced in Java 17?

Already covered in Set 46.

GeoOps currently uses Java 17 and already contains modern Java evidence through the `GeoProject` record.

The repository does not claim every Java 17 feature is used.

### ✅ 4. Are you aware of recent Java updates such as Records and Sealed Classes?

Already covered in Set 46.

GeoOps currently uses a record for `GeoProject`.

A sealed hierarchy is still not introduced because there is no established domain requirement for one.

---

## What "currently working on" means in GeoOps

The current project version is not inferred only from source syntax.

A developer can verify the expected environment with:

~~~bash
java -version
mvn -version
~~~

and then run:

~~~bash
mvn clean verify
~~~

The Maven Enforcer rule ensures a non-Java-17 runtime does not silently build the project.

That gives one consistent answer across:

~~~text
developer machine
     ↓
Maven build
     ↓
tests
     ↓
GitHub Actions
     ↓
GeoOps runtime expectation

Java 17
~~~

## Why Set 47 does not add more Java-version code

The source anchor is effectively a present-tense version check.

Set 46 already:
- pinned Java 17,
- enforced Java 17,
- tested Java 17,
- documented Java 17,
- connected Java 17 to existing record usage.

Adding another plugin, endpoint, or test solely because the wording changed from "which version do you use" to "which version are you currently working on" would duplicate project logic.

Set 47 therefore reuses the verified Set 46 evidence.

## Set 47 world decision

- The current day-to-day GeoOps Java version is Java 17.
- The current version is the same enforced baseline established in Set 46.
- No Java-version migration occurred between Sets 46 and 47.
- All four surrounding technical questions are previously completed and are reused with ✅.
- No new production code is required.
- No new synthetic technical question is required.
- Unique master technical-question coverage remains 203.
- Synthetic technical-question coverage remains 10.
- The separate future anchor "What's the Java version you are using?" remains untouched.

---

## Experience Answer

Currently, I’m working with **Java 17** in the GeoOps project. We keep that consistent across local development, Maven, and CI: the repository has a `.java-version` file set to 17, Maven compiles for Java 17 and rejects the wrong runtime through the Enforcer plugin, and GitHub Actions runs with Temurin 17.

So when I say Java 17 is the version I’m currently working on, it is not just an IDE setting—the build and test pipeline enforce the same version as well.
