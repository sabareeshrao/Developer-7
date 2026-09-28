# Set 1 — Development Environment and Spring Boot Bootstrap

**Status:** 1/387+  
**Anchor:** ⭐ What's your preferred development environment and tool set for Spring Boot application?

This document is the repository evidence pack for Set 1. Another AI should be able to answer every question in this set by reading this file together with the referenced code.

## World decision established by this set

For the fictional AtlasGrid Geospatial Systems GeoOps project, the development baseline is:

- IntelliJ IDEA as the primary IDE
- Java 17 JDK
- Maven as the build and dependency-management tool
- Spring Boot 3.3.5
- Spring MVC through `spring-boot-starter-web`
- Bean Validation through `spring-boot-starter-validation`
- Spring Boot Actuator for health/info endpoints
- Lombok for selected boilerplate reduction
- JUnit 5 / Spring Boot Test for testing
- embedded Tomcat supplied by the web starter
- Git/GitHub for source control

The project begins as a small Spring Boot service and will grow organically as later interview anchors add databases, GIS processing, security, messaging, deployment, monitoring and production scenarios.

## Evidence map

| Concept | Repository evidence |
|---|---|
| Java version | `.java-version`, `pom.xml -> <java.version>17</java.version>` |
| Maven project | `pom.xml` |
| Spring Boot parent | `pom.xml -> spring-boot-starter-parent` |
| Dependency management | Spring Boot parent manages compatible dependency versions |
| Starter dependencies | web, validation, actuator, test in `pom.xml` |
| Lombok | Lombok dependency in `pom.xml`; `@Slf4j` in `ProjectService` |
| Spring Boot entry point | `GeoOpsApplication.java` |
| `@SpringBootApplication` | `GeoOpsApplication.java` |
| `SpringApplication.run()` | `GeoOpsApplication.main()` |
| Controller layer | `ProjectController.java` |
| Service/application layer | `ProjectService.java` |
| Domain layer | `GeoProject.java` |
| Configuration | `src/main/resources/application.yml` |
| Testing | `GeoOpsApplicationTests.java` |
| Runnable artifact | `spring-boot-maven-plugin` in `pom.xml` |

---

## Part A

### 1. How can we add Lombok in IntelliJ or whatever IDE?

In this project Lombok is added as a Maven dependency in `pom.xml`. IntelliJ reads the Maven model and downloads the dependency into the local Maven repository. IntelliJ must also have annotation processing enabled so Lombok can generate code during compilation/IDE analysis.

The concrete proof is `@Slf4j` in `ProjectService`. Lombok generates the logger field used by `log.info(...)`.

### 2. What is the role of the JVM in making Java platform-independent?

Java source is compiled by the JDK compiler into JVM bytecode. The same bytecode can run on different operating systems as long as each machine has a compatible JVM. The platform-specific work is handled by that JVM implementation.

GeoOps uses Java 17 bytecode/runtime expectations, established in `.java-version` and `pom.xml`.

### 3. Can a machine have multiple versions of JDK or JRE installed?

Yes. A developer machine can contain several JDKs. IntelliJ lets a project select its Project SDK, and shell tools use `JAVA_HOME` / `PATH`. GeoOps deliberately pins Java 17 so the IDE, Maven and CI should all use the same major version.

### 4. Can you tell me what JVM is and how it works?

The JVM loads compiled `.class` bytecode, verifies it, manages memory, executes bytecode using interpretation/JIT compilation, performs garbage collection, and provides runtime services such as threads and class loading.

For GeoOps the JVM ultimately executes `GeoOpsApplication.main()`, after which Spring Boot creates the application context and starts the embedded web server.

### 5. Can you tell me the difference between JDK, JRE and JVM?

- JVM: executes Java bytecode.
- JRE: JVM plus runtime libraries needed to run Java applications.
- JDK: development kit containing the runtime plus tools such as `javac`, `jar`, and diagnostic utilities.

A GeoOps developer needs a Java 17 JDK because we compile, test and package the application, not merely run precompiled bytecode.

### 6. Do you know about Maven, like what Maven is and why are we using Maven in our project?

Maven is GeoOps' build and dependency-management tool. It gives the project a standard structure, resolves libraries declared in `pom.xml`, runs tests, compiles Java, packages the executable JAR, and supports repeatable local/CI builds.

Typical commands:

```bash
mvn clean test
mvn spring-boot:run
mvn clean package
```

### 7. What is the role of pom.xml in a Spring Boot project?

`pom.xml` is the Maven Project Object Model. In GeoOps it defines:

- project coordinates: `com.atlasgrid:geoops:0.0.1-SNAPSHOT`
- Java 17
- Spring Boot parent version
- dependencies
- test dependencies
- Spring Boot Maven plugin used to package/run the app

---

## Part B

### 8. What are Dependencies and why do we need them?

Dependencies are external libraries used by the application instead of reimplementing infrastructure. GeoOps currently depends on Spring Web, Validation, Actuator, Lombok and Spring Boot Test.

For example, `spring-boot-starter-web` supplies Spring MVC, JSON serialization and embedded Tomcat, allowing `ProjectController` to expose REST endpoints.

### 9. Can you explain the Maven lifecycle and its phases?

The main lifecycle flows through phases such as validate → compile → test → package → verify → install → deploy.

For GeoOps:
- `compile` compiles Java source.
- `test` runs `GeoOpsApplicationTests`.
- `package` builds the executable JAR.
- `install` also puts the artifact into the developer's local Maven repository.

`clean` belongs to a separate lifecycle and removes generated build output such as `target/`.

### 10. What is the difference between Maven Local Repository and Central Repository?

Maven Central is a remote repository from which public dependencies can be downloaded. The local repository is the developer-machine cache, normally under `~/.m2/repository`.

On the first GeoOps build Maven downloads missing Spring Boot/Lombok artifacts; later builds can reuse the locally cached copies.

### 11. Do you know about the .m2 folder?

`.m2` is Maven's user-level working directory. Its `repository` subfolder caches dependencies and locally installed artifacts. It may also contain `settings.xml` for repository credentials, mirrors, proxies and profiles.

The `.m2` directory is not committed to this repository because it is machine-specific.

### 12. What are the advantages of using Spring Boot over a traditional Spring application?

Spring Boot reduces setup by providing:
- auto-configuration
- starter dependencies
- sensible defaults
- embedded server support
- production helpers such as Actuator
- executable JAR packaging

That lets GeoOps begin with `GeoOpsApplication` and a small `application.yml` rather than manually wiring a servlet container and large amounts of XML/configuration.

### 13. What is the role of @SpringBootApplication annotation in a Spring Boot application?

It marks the main configuration class and triggers the common Spring Boot startup behavior. In GeoOps it appears on `GeoOpsApplication`, making that class the bootstrap root of the application.

### 14. What are the components that make up @SpringBootApplication annotation?

Conceptually it combines:
- `@SpringBootConfiguration` / configuration semantics
- `@EnableAutoConfiguration`
- `@ComponentScan`

Because `GeoOpsApplication` is in package `com.atlasgrid.geoops`, component scanning discovers classes underneath it such as `ProjectController` and `ProjectService`.

---

## Part C

### 15. What is Spring Boot dependency management?

Spring Boot publishes a curated dependency set whose versions are known to work together. GeoOps inherits from `spring-boot-starter-parent`, so dependencies such as the Spring starters and Lombok generally do not need individual version declarations.

### 16. What are Spring Boot Starter dependencies?

Starters are convenient dependency bundles for common application capabilities.

GeoOps uses:
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-actuator`
- `spring-boot-starter-test`

They pull in the coordinated libraries necessary for those capabilities.

### 17. What happens internally when we start a Spring Boot application?

At a high level:
1. the JVM invokes `GeoOpsApplication.main()`;
2. `SpringApplication.run()` bootstraps Spring;
3. configuration and classpath are inspected;
4. component scanning finds application beans;
5. auto-configuration creates infrastructure beans;
6. the application context is refreshed;
7. embedded Tomcat starts because the web starter is present;
8. REST mappings such as `/api/projects` become available.

### 18. What role does SpringApplication.run() play?

`SpringApplication.run(GeoOpsApplication.class, args)` is the handoff from ordinary Java startup to Spring Boot. It prepares the environment, creates the appropriate application context, loads beans, applies auto-configuration and starts the web application.

---

## Development workflow

### IntelliJ

1. Open the repository as a project.
2. Select a Java 17 JDK as the Project SDK.
3. Let IntelliJ import the Maven `pom.xml`.
4. Enable annotation processing for Lombok.
5. Run `GeoOpsApplication.main()`, or run Maven from the integrated terminal.

### Command line

```bash
java -version
mvn -version
mvn clean test
mvn spring-boot:run
```

Then test:

```bash
curl http://localhost:8080/actuator/health

curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{
        "projectCode":"TX-AUS-001",
        "name":"Austin Survey Intake",
        "coordinateReferenceSystem":"EPSG:4326"
      }'

curl http://localhost:8080/api/projects
```

## Why the codebase is intentionally small in Set 1

Set 1 proves the development environment and Spring Boot bootstrap. It does not prematurely introduce PostgreSQL/PostGIS, security, messaging, Docker or cloud infrastructure. Those technologies will be added when their own job-experience anchors require them, preserving the natural learning/world-building sequence.
