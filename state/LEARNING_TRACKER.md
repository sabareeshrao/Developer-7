# Learning Tracker

## Counter rule
- Display status as current anchor / total anchors+.
- Original anchor pool starts at 387.
- Each synthetic GIS job-experience anchor permanently increases the denominator by 1.
- Example: after one synthetic anchor exists, status uses /388+.
- The + means the world may continue expanding with future synthetic anchors.

## Legend
- ⭐ = original job-experience anchor from the 387-question source bank
- ⭐⭐ = synthetic GIS job-experience anchor created to complete the developer/codebase flow
- 💡 = synthetic technical question created because the GIS/codebase flow needs it and no suitable question exists in the 2,308-question master bank
- ✅ = technical question already completed in an earlier anchor and reused later; do not reteach it
- no emoji = related technical question from the 2,308-question master bank
- [ ] = not covered
- [x] = covered

## One-anchor-per-set rule
Each set contains exactly ONE ⭐ or ⭐⭐ job-experience anchor.
Only technical questions directly surrounding that anchor may appear beneath it.
If an anchor has more than 7 related technical questions, divide them into Part A, Part B, Part C, etc.
Each part may contain at most 7 related technical questions.

---

# Set 1

**Status: 1/387+**

- [x] ⭐ [Master 4] What's your preferred development environment and tool set for Spring Boot application?

## Part A
- [x] [Master 3] How can we add Lombok in IntelliJ or whatever IDE?
- [x] [Master 13] What is the role of the JVM in making Java platform-independent?
- [x] [Master 14] Can a machine have multiple versions of JDK or JRE installed?
- [x] [Master 15] Can you tell me what JVM is and how it works?
- [x] [Master 17] Can you tell me the difference between JDK, JRE and JVM?
- [x] [Master 1220] Do you know about Maven, like what Maven is and why are we using Maven in our project?
- [x] [Master 1214] What is the role of pom.xml in a Spring Boot project?

## Part B
- [x] [Master 1250] What are Dependencies and why do we need them?
- [x] [Master 1219] Can you explain the Maven lifecycle and its phases?
- [x] [Master 1240] What is the difference between Maven Local Repository and Central Repository?
- [x] [Master 1241] Do you know about the .m2 folder?
- [x] [Master 1694] What are the advantages of using Spring Boot over a traditional Spring application?
- [x] [Master 1697] What is the role of @SpringBootApplication annotation in a Spring Boot application?
- [x] [Master 1698] What are the components that make up @SpringBootApplication annotation?

## Part C
- [x] [Master 1709] What is Spring Boot dependency management?
- [x] [Master 1710] What are Spring Boot Starter dependencies?
- [x] [Master 1763] What happens internally when we start a Spring Boot application?
- [x] [Master 1764] What role does SpringApplication.run() play?

## Set 1 completion evidence
- docs/sets/SET-001-DEVELOPMENT-ENVIRONMENT.md
- pom.xml
- .java-version
- src/main/java/com/atlasgrid/geoops/GeoOpsApplication.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/domain/GeoProject.java
- src/main/resources/application.yml
- src/test/java/com/atlasgrid/geoops/GeoOpsApplicationTests.java
- .github/workflows/ci.yml

Set 1 anchor count: 1
Set 1 learning items completed: 19 / 19
Synthetic job-experience anchors created so far: 0
Synthetic technical questions created so far: 0
Current denominator: 387+
