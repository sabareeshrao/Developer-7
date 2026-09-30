# ADR — Java 17 Runtime and Build Baseline

## Status

Accepted in Set 46.

## Decision

GeoOps uses **Java 17** as its current development, build, test, CI, and runtime baseline.

This ADR records the version contract only. The separate interview question about *why* Java 17 was chosen is intentionally left for its own future job-experience anchor.

## Repository enforcement

The Java 17 baseline is represented in four places:

1. `.java-version`

~~~text
17
~~~

2. Maven compiler configuration

~~~xml
<java.version>17</java.version>
<maven.compiler.release>17</maven.compiler.release>
~~~

3. Maven Enforcer

~~~xml
<requireJavaVersion>
    <version>[17,18)</version>
</requireJavaVersion>
~~~

A developer or CI agent using a runtime outside Java 17 fails before the normal build proceeds.

4. GitHub Actions

~~~yaml
- name: Set up Java 17
  uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: "17"
~~~

## Executable proof

`Java17BaselineTest` checks:

~~~java
Runtime.version().feature() == 17
~~~

and also verifies that the current `GeoProject` domain type is a Java `record`.

## Modern-language evidence

GeoOps already contains a Java record:

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

Records are available on the Java 17 baseline and are used for this immutable data-oriented domain model.

GeoOps does **not** currently use a sealed hierarchy. Set 46 documents awareness of sealed classes without adding one artificially.

## Consequence

Developers, local Maven builds, and CI are expected to use Java 17 consistently. A future version migration must explicitly update:
- `.java-version`,
- Maven compiler/enforcer configuration,
- GitHub Actions,
- tests,
- canon and migration documentation.

## Set 49 rationale — why Java 17 even without heavy Java-17-specific syntax

The project does **not** choose Java 17 only to gain access to new syntax.

The rationale now recorded for GeoOps is:

1. **Spring Boot 3.x compatibility baseline**
   - GeoOps uses Spring Boot 3.3.5 in `pom.xml`.
   - The Spring Boot 3.3 line requires at least Java 17.
   - Therefore Java 17 is part of the framework/runtime compatibility baseline, not merely a language-feature preference.

2. **Long-Term Support baseline**
   - Java 17 is an LTS release.
   - An LTS baseline gives an enterprise project a conservative, well-supported runtime target even if the application does not immediately adopt every feature introduced by that release.

3. **Consistent toolchain**
   - Local development, Maven, CI and runtime tests all use the same Java 17 contract.
   - Avoiding version drift is valuable independently of language-feature usage.

4. **Modern platform access without forced feature adoption**
   - Running on Java 17 makes modern Java APIs/language capabilities available.
   - The codebase can adopt a feature when it improves the design.
   - GeoOps already uses a `record`, but deliberately does not add sealed classes or other constructs merely to claim Java 17 usage.

5. **Version choice and feature choice are separate decisions**
   - Runtime/framework compatibility answers "which baseline should the project run on?"
   - Code readability/domain need answers "which language feature should this class use?"
   - A project does not need to use many Java-17-specific constructs to justify a Java 17 runtime baseline.

### External verification used for this rationale

Spring Boot 3.3 system requirements:

https://docs.spring.io/spring-boot/3.3/system-requirements.html

Oracle Java 17 announcement / LTS description:

https://www.oracle.com/news/announcement/oracle-releases-java-17-2021-09-14/

These references support the compatibility/LTS facts. The GeoOps-specific decision remains documented by this ADR and the repository's build configuration.

### Non-claims

This ADR does **not** claim:
- that GeoOps migrated from Java 8, 11, or another version;
- that a customer mandated Java 17;
- that Java 17 produced a measured performance percentage;
- that every Java 17 language feature is used;
- that Java 17 was selected because of one isolated syntax feature.

Any future migration story must be introduced by its own source anchor and repository evidence.

## Post-Set-50 maintenance evolution

The historical Set 49 rationale referenced Spring Boot 3.3.5 because that was the project framework version at that time.

After Set 50, the project was upgraded to **Spring Boot 4.1.1** as a maintenance change.

The Java baseline did **not** change:

```text
Java 17
→ still enforced by Maven compiler release
→ still enforced by Maven Enforcer [17,18)
→ still installed by GitHub Actions
→ still verified by Java17BaselineTest
```

Spring Boot 4.1.1 still has Java 17 as its minimum Java version, so the original Java 17 platform decision remains compatible with the maintained framework baseline.

Current Spring Boot system requirements:

https://docs.spring.io/spring-boot/system-requirements.html

This framework upgrade is maintenance evolution, not a claim that a historical job-experience migration occurred during Sets 46–49.

