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
