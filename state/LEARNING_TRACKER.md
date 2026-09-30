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

Set 1 learning items completed: 19 / 19

---

# Set 2

**Status: 2/387+**

- [x] ⭐ [Master 39] Did you get a chance to use System.exit() in your project?

## Part A
- [x] ✅ [Master 15] Can you tell me what JVM is and how it works?
- [x] [Master 18] Can you explain public static void main(String[] args) and why each term is used?
- [x] [Master 37] Do you know about System.exit() in Java?
- [x] [Master 38] What happens internally when System.exit() is called?
- [x] [Master 323] What is finally block?
- [x] [Master 342] Can you tell me a condition where the finally block will not be executed?
- [x] 💡 Why should normal Spring Boot request/service code prefer graceful shutdown over calling System.exit() directly?

## Set 2 completion evidence
- docs/sets/SET-002-SYSTEM-EXIT.md
- src/main/java/com/atlasgrid/geoops/tools/preflight/GeoOpsPreflightCli.java
- src/main/java/com/atlasgrid/geoops/tools/preflight/DatasetPreflightValidator.java
- src/main/java/com/atlasgrid/geoops/tools/preflight/PreflightResult.java
- src/test/java/com/atlasgrid/geoops/tools/preflight/DatasetPreflightValidatorTest.java
- src/main/resources/application.yml

Set 2 learning items completed: 8 / 8

---

# Set 3

**Status: 3/387+**

- [x] ⭐ [Master 40] Can you tell me your project methodology? Is it based on Agile or Waterfall model?

## Part A
- [x] 💡 What is Agile and why would a software team choose it over Waterfall?
- [x] 💡 What is Scrum and how does it organize Agile work into sprints?
- [x] 💡 What is a user story and how is it different from a task?
- [x] 💡 What are acceptance criteria and why do they matter before development starts?
- [x] 💡 What happens during Sprint Planning, Daily Stand-up, Sprint Review, and Retrospective?
- [x] 💡 What are Definition of Ready and Definition of Done?
- [x] 💡 How does GitHub issue → branch → pull request → CI map to an Agile sprint in GeoOps?

## Set 3 completion evidence
- docs/sets/SET-003-PROJECT-METHODOLOGY.md
- docs/process/AGILE-WORKFLOW.md
- docs/process/DEFINITION-OF-DONE.md
- docs/process/SPRINT-001.md
- .github/ISSUE_TEMPLATE/feature.yml
- .github/pull_request_template.md
- .github/workflows/ci.yml

Set 3 learning items completed: 8 / 8

---

# Set 4

**Status: 4/387+**

- [x] ⭐ [Master 62] Have you worked with StringBuilder and StringBuffer?

## Part A
- [x] [Master 50] Can you discuss a scenario where StringBuilder is preferable over StringBuffer?
- [x] [Master 51] If you want a mutable version of String, what would you use?
- [x] [Master 52] Why are you not using StringBuffer?
- [x] [Master 55] What happens internally when you concatenate two String objects using the + operator?
- [x] [Master 2184] A web server handles thousands of requests involving String manipulation. What would you choose among String, StringBuilder and StringBuffer, and which would perform better?

## Set 4 completion evidence
- docs/sets/SET-004-STRINGBUILDER-STRINGBUFFER.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectManifestFormatter.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectManifestFormatterTest.java

Set 4 learning items completed: 6 / 6

---

# Set 5

**Status: 5/387+**

- [x] ⭐ [Master 85] What's the use of object-oriented programming in enterprise projects?

## Part A
- [x] [Master 69] Can you explain the concepts of classes and objects?
- [x] [Master 75] Why was OOP introduced in Java? Don't tell me the four pillars, just tell me why OOP was introduced and what benefits it provides.
- [x] [Master 66] What is meant by Code Reusability in OOP?
- [x] [Master 78] How does abstraction differ from encapsulation?
- [x] [Master 83] What is the difference between Association, Aggregation and Composition?
- [x] [Master 111] How does encapsulation enhance software security and integrity?
- [x] [Master 169] Can you explain inheritance and composition in Java?

## Part B
- [x] [Master 186] How does Java achieve polymorphism?
- [x] [Master 221] How does abstraction help in achieving loose coupling in a software application?
- [x] [Master 1711] Can you tell me how OOP is helpful in Spring Boot projects?

## Set 5 completion evidence
- docs/sets/SET-005-OOP-ENTERPRISE.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationRule.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectCodeValidationRule.java
- src/main/java/com/atlasgrid/geoops/project/validation/CoordinateReferenceSystemValidationRule.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationService.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationReport.java
- src/main/java/com/atlasgrid/geoops/project/validation/ValidationIssue.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/validation/ProjectValidationServiceTest.java

Set 5 learning items completed: 11 / 11

---

# Set 6

**Status: 6/387+**

- [x] ⭐ [Master 150] Have you used the final keyword in your project ever?

## Part A
- [x] [Master 121] What is the difference between final, Effectively Final and Immutable?
- [x] [Master 124] Can we modify a final object reference?
- [x] [Master 127] Can you explain the final keyword for a final variable, final method and final class?
- [x] [Master 129] What happens when we use final with a method?
- [x] [Master 130] Discuss a scenario where the final keyword significantly impacts the design of a Java program.
- [x] [Master 160] What's the impact of declaring a method as final on inheritance?

## Set 6 completion evidence
- docs/sets/SET-006-FINAL-KEYWORD.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationStandards.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectCodeValidationRule.java
- src/main/java/com/atlasgrid/geoops/project/validation/CoordinateReferenceSystemValidationRule.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationService.java

Set 6 learning items completed: 7 / 7

---

# Set 7

**Status: 7/387+**

- [x] ⭐ [Master 151] Can you tell me a real-world or real-time use case of the final keyword?

## Part A
- [x] ✅ [Master 121] What is the difference between final, Effectively Final and Immutable?
- [x] ✅ [Master 124] Can we modify a final object reference?
- [x] ✅ [Master 127] Can you explain the final keyword for a final variable, final method and final class?
- [x] [Master 123] Can a Class be both final and abstract at the same time?
- [x] ✅ [Master 130] Discuss a scenario where the final keyword significantly impacts the design of a Java program.
- [x] ✅ [Master 160] What's the impact of declaring a method as final on inheritance?
- [x] [Master 224] Can an Interface be declared final?

## Set 7 completion evidence
- docs/sets/SET-007-FINAL-REAL-WORLD.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectServiceFinalReferenceTest.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationStandards.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectCodeValidationRule.java
- src/main/java/com/atlasgrid/geoops/project/validation/CoordinateReferenceSystemValidationRule.java

Set 7 learning items completed: 8 / 8

---

# Set 8

**Status: 8/387+**

- [x] ⭐ [Master 152] Have you written any static methods?

## Part A
- [x] ✅ [Master 18] Can you explain public static void main(String[] args) and why each term is used?
- [x] [Master 140] Are you aware of the static keyword in Java?
- [x] [Master 145] What's the use of a static method? Why do we use it?
- [x] [Master 144] Can we call a non-static instance variable or method from a static method? Why can't we call it directly?
- [x] [Master 141] Can you override static methods in Java?
- [x] [Master 148] What happens if you call a Static Method using a null Object Reference?
- [x] [Master 149] What happens when a static main() method directly calls a non-static method without creating an Object?

## Set 8 completion evidence
- docs/sets/SET-008-STATIC-METHODS.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationStandards.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectCodeValidationRule.java
- src/main/java/com/atlasgrid/geoops/project/validation/CoordinateReferenceSystemValidationRule.java
- src/test/java/com/atlasgrid/geoops/project/validation/ProjectValidationStandardsTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 8 learning items completed: 8 / 8

---

# Set 9

**Status: 9/387+**

- [x] ⭐ [Master 269] Have you overridden hashCode() and equals() before?

## Part A
- [x] [Master 244] What methods are available in the Java Object class, and how are they used?
- [x] [Master 246] How would you handle a situation where you need to compare the content equality of two custom object instances?
- [x] [Master 248] Why is it important to override hashCode() when you are overriding equals()?
- [x] [Master 249] Can you describe how hashCode() and equals() work together in Collections?
- [x] [Master 256] Can you tell me a scenario where we should override hashCode() and equals()?
- [x] [Master 257] How can we override hashCode() and equals()? Can you tell me the steps?
- [x] [Master 260] Do you know the contract between hashCode() and equals()?

## Set 9 completion evidence
- docs/sets/SET-009-EQUALS-HASHCODE.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/project/domain/ProjectIdentity.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/application/DuplicateProjectException.java
- src/test/java/com/atlasgrid/geoops/project/domain/ProjectIdentityTest.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectServiceDuplicateTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 9 learning items completed: 8 / 8

---

# Set 10

**Status: 10/387+**

- [x] ⭐ [Master 270] Have you used == and .equals() operators in your project?

## Part A
- [x] ✅ [Master 246] How would you handle a situation where you need to compare the content equality of two custom object instances?
- [x] [Master 262] What's the difference between == and .equals() in Java?
- [x] [Master 266] How does the equals() method in String work, and how is it different from the == operator?
- [x] [Master 267] In a user-authentication module, what issues could arise if we use == instead of equals() to compare credentials?
- [x] [Master 302] Two Integer variables, A and B, both contain 200. What will A == B return, true or false?
- [x] [Master 2500] Let's say Integer A = 127 and Integer B = 127. If you do A == B, will it return true or false?
- [x] [Master 2501] If Integer C = 128 and Integer D = 128, will C == D print true or false?

## Set 10 completion evidence
- docs/sets/SET-010-EQUALITY-OPERATORS.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/project/domain/ProjectIdentity.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectServiceEqualityOperatorTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 10 learning items completed: 8 / 8

---

# Set 11

**Status: 11/387+**

- [x] ⭐ [Master 304] Have you ever got a chance to design an immutable class?

## Part A
- [x] [Master 291] What does immutability mean in Java?
- [x] [Master 283] What makes an object immutable in Java?
- [x] [Master 275] Can we create immutable classes in Java?
- [x] [Master 276] How can we create an immutable class?
- [x] [Master 282] Why should we not have setter methods in an immutable class?
- [x] [Master 294] You need to design an immutable class that contains a mutable object such as a List. How would you design it?
- [x] [Master 303] Let's say you need to ensure that certain data within your application remains constant and secure throughout its lifecycle. How would you implement immutability for this purpose?

## Set 11 completion evidence
- docs/sets/SET-011-IMMUTABLE-CLASS.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/project/domain/ProjectCatalogSnapshot.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/domain/ProjectCatalogSnapshotTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 11 learning items completed: 8 / 8

---

# Set 12

**Status: 12/387+**

- [x] ⭐ [Master 319] Have you worked with Enum in your project?

## Part A
- [x] [Master 314] Can you explain Enum in Java?
- [x] [Master 315] What's the purpose of using Enum?
- [x] [Master 313] Can you tell me the advantages of Enum over constants?
- [x] [Master 2724] What is an enum in Java, and how is it different from a set of constants?
- [x] [Master 316] Let's say you are working in a payment system with different methods like credit card, UPI and net banking. How would you use Enums and what would be your strategy?
- [x] [Master 318] Can Enums implement interfaces?

## Set 12 completion evidence
- docs/sets/SET-012-ENUM.md
- docs/process/SPRINT-002.md
- src/main/java/com/atlasgrid/geoops/tools/preflight/DatasetFormat.java
- src/main/java/com/atlasgrid/geoops/tools/preflight/DatasetPreflightValidator.java
- src/test/java/com/atlasgrid/geoops/tools/preflight/DatasetFormatTest.java
- src/test/java/com/atlasgrid/geoops/tools/preflight/DatasetPreflightValidatorTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 12 learning items completed: 7 / 7

---

# Set 13

**Status: 13/387+**

- [x] ⭐ [Master 366] Can you share some custom exception names that you guys are throwing in your current project?

## Part A
- [x] [Master 320] What are the steps to create a custom exception?
- [x] [Master 324] Do you know about custom exceptions and inbuilt exceptions?
- [x] [Master 325] Which are better, custom exceptions or built-in exceptions?
- [x] [Master 327] Can you discuss exception handling and what are checked and unchecked exceptions?
- [x] [Master 337] How do you create a custom runtime exception?
- [x] [Master 338] How do you handle exceptions globally in a Spring Boot application?
- [x] [Master 347] In what scenarios would you create a custom checked Exception versus a custom unchecked Exception?

## Set 13 completion evidence
- docs/sets/SET-013-CUSTOM-EXCEPTIONS.md
- docs/process/SPRINT-003.md
- src/main/java/com/atlasgrid/geoops/project/application/DuplicateProjectException.java
- src/main/java/com/atlasgrid/geoops/project/application/InvalidProjectRequestException.java
- src/main/java/com/atlasgrid/geoops/project/api/GeoOpsExceptionHandler.java
- src/main/java/com/atlasgrid/geoops/project/api/ApiError.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectServiceValidationExceptionTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectControllerExceptionIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 13 learning items completed: 8 / 8

---

# Set 14

**Status: 14/387+**

- [x] ⭐ [Master 368] Have you ever created a Custom Exception Hierarchy?

## Part A
- [x] ✅ [Master 327] Can you discuss exception handling and what are checked and unchecked exceptions?
- [x] ✅ [Master 337] How do you create a custom runtime exception?
- [x] ✅ [Master 347] In what scenarios would you create a custom checked Exception versus a custom unchecked Exception?
- [x] [Master 330] Do you know the difference between Error, RuntimeException, and Exception?
- [x] [Master 351] What is the difference between Throwable and Exception in Java?
- [x] [Master 350] What is Exception Chaining, and why is it important in our projects?
- [x] [Master 365] If @RestControllerAdvice contains both a Generic Exception Handler and a Specific Exception Handler, which one will execute?

## Set 14 completion evidence
- docs/sets/SET-014-CUSTOM-EXCEPTION-HIERARCHY.md
- docs/process/SPRINT-003.md
- src/main/java/com/atlasgrid/geoops/project/application/GeoOpsProjectException.java
- src/main/java/com/atlasgrid/geoops/project/application/DuplicateProjectException.java
- src/main/java/com/atlasgrid/geoops/project/application/InvalidProjectRequestException.java
- src/main/java/com/atlasgrid/geoops/project/api/GeoOpsExceptionHandler.java
- src/main/java/com/atlasgrid/geoops/project/api/ApiError.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectExceptionHierarchyTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectControllerExceptionIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 14 learning items completed: 8 / 8

---

# Set 15

**Status: 15/387+**

- [x] ⭐ [Master 369] How do you handle exceptions in your project?

## Part A
- [x] ✅ [Master 327] Can you discuss exception handling and what are checked and unchecked exceptions?
- [x] ✅ [Master 338] How do you handle exceptions globally in a Spring Boot application?
- [x] ✅ [Master 365] If @RestControllerAdvice contains both a Generic Exception Handler and a Specific Exception Handler, which one will execute?
- [x] [Master 339] Can you explain the role of try, catch and finally blocks?
- [x] [Master 344] How would you handle multiple Exceptions in a single catch block?
- [x] [Master 348] How does Exception Propagation work in Java?
- [x] [Master 364] What is the difference between @ControllerAdvice and @RestControllerAdvice?

## Set 15 completion evidence
- docs/sets/SET-015-EXCEPTION-HANDLING.md
- docs/process/SPRINT-003.md
- src/main/java/com/atlasgrid/geoops/project/api/GeoOpsExceptionHandler.java
- src/main/java/com/atlasgrid/geoops/tools/preflight/DatasetPreflightValidator.java
- src/test/java/com/atlasgrid/geoops/project/api/GeoOpsExceptionHandlerTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectControllerExceptionIntegrationTest.java
- src/test/java/com/atlasgrid/geoops/tools/preflight/DatasetPreflightValidatorTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 15 learning items completed: 8 / 8

---

# Set 16

**Status: 16/387+**

- [x] ⭐ [Master 370] What strategies do you majorly use for exception handling?

## Part A
- [x] ✅ [Master 338] How do you handle exceptions globally in a Spring Boot application?
- [x] ✅ [Master 348] How does Exception Propagation work in Java?
- [x] ✅ [Master 339] Can you explain the role of try, catch and finally blocks?
- [x] [Master 334] Is it good practice to use exceptions for control flow?
- [x] [Master 336] Do you know about uncaught exceptions?
- [x] [Master 328] How would you handle a scenario where a method throws multiple types of exception?
- [x] [Master 363] You created a @RestControllerAdvice, but the Global Exception is not being caught. What could be the reasons?

## Set 16 completion evidence
- docs/sets/SET-016-EXCEPTION-HANDLING-STRATEGIES.md
- docs/process/SPRINT-003.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectServiceLookupStrategyTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectControllerLookupIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 16 learning items completed: 8 / 8

---

# Set 17

**Status: 17/387+**

- [x] ⭐ [Master 371] What were your basic approaches to error handling and what were the basic things that you were doing in error handling?

## Part A
- [x] ✅ [Master 338] How do you handle exceptions globally in a Spring Boot application?
- [x] ✅ [Master 339] Can you explain the role of try, catch and finally blocks?
- [x] ✅ [Master 344] How would you handle multiple Exceptions in a single catch block?
- [x] [Master 321] What is NullPointerException, and how can we avoid it?
- [x] [Master 340] If a return statement executes inside the try or catch block, does the finally block still execute?
- [x] [Master 341] Is it possible to execute a program with a try block but without a catch block?
- [x] [Master 356] While designing a File Handling module, how would you decide which Exceptions should be Checked and which should be Unchecked?

## Set 17 completion evidence
- docs/sets/SET-017-BASIC-ERROR-HANDLING.md
- docs/process/SPRINT-003.md
- src/main/java/com/atlasgrid/geoops/tools/preflight/DatasetPreflightValidator.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationStandards.java
- src/test/java/com/atlasgrid/geoops/tools/preflight/DatasetPreflightValidatorTest.java
- src/test/java/com/atlasgrid/geoops/project/validation/ProjectValidationStandardsTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 17 learning items completed: 8 / 8

---

# Set 18

**Status: 18/387+**

- [x] ⭐ [Master 372] Can you tell me how you guys are maintaining or handling exceptions?

## Part A
- [x] ✅ [Master 338] How do you handle exceptions globally in a Spring Boot application?
- [x] ✅ [Master 350] What is Exception Chaining, and why is it important in our projects?
- [x] ✅ [Master 365] If @RestControllerAdvice contains both a Generic Exception Handler and a Specific Exception Handler, which one will execute?
- [x] [Master 322] What is the role of the pipe (|) symbol in a multi-catch block?
- [x] [Master 326] Can you name any inbuilt exception?
- [x] [Master 349] Suppose you have a method that throws a Checked Exception, but the Interface it implements does not declare that Exception. How would you handle this situation?
- [x] [Master 362] Can we have multiple @ControllerAdvice annotations?

## Set 18 completion evidence
- docs/sets/SET-018-EXCEPTION-MAINTENANCE.md
- docs/process/SPRINT-003.md
- src/main/java/com/atlasgrid/geoops/error/GeoOpsErrorCode.java
- src/main/java/com/atlasgrid/geoops/project/application/GeoOpsProjectException.java
- src/main/java/com/atlasgrid/geoops/project/application/DuplicateProjectException.java
- src/main/java/com/atlasgrid/geoops/project/application/InvalidProjectRequestException.java
- src/main/java/com/atlasgrid/geoops/project/api/ApiError.java
- src/main/java/com/atlasgrid/geoops/project/api/GeoOpsExceptionHandler.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectExceptionHierarchyTest.java
- src/test/java/com/atlasgrid/geoops/project/api/GeoOpsExceptionHandlerTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 18 learning items completed: 8 / 8


---

# Set 19

**Status: 19/387+**

- [x] ⭐ [Master 374] Was there ever a time when the finally block caused unexpected behavior or side effects in your code?

## Part A
- [x] ✅ [Master 339] Can you explain the role of try, catch and finally blocks?
- [x] ✅ [Master 340] If a return statement executes inside the try or catch block, does the finally block still execute?
- [x] [Master 352] If an Exception is thrown inside a finally block, will it override an Exception thrown from the try block?
- [x] [Master 354] What happens if both the try and finally blocks contain return statements?
- [x] [Master 357] Can we throw an exception from a finally block?
- [x] [Master 358] Can we have multiple finally blocks in Java?
- [x] [Master 360] Which finally block will be executed if we have multiple?

## Set 19 completion evidence
- docs/sets/SET-019-FINALLY-SIDE-EFFECTS.md
- docs/process/SPRINT-003.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectManifestFileExporter.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectManifestFileExporterTest.java
- src/test/java/com/atlasgrid/geoops/project/application/FinallyBlockBehaviorTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 19 learning items completed: 8 / 8


---

# Set 20

**Status: 20/387+**

- [x] ⭐ [Master 403] Have you worked with collections in Java?

## Part A
- [x] [Master 389] Can you explain the concept of the Java Collection Framework?
- [x] [Master 375] What are the major collections we have?
- [x] [Master 379] What are the main implementations of the List interface?
- [x] [Master 380] What is the difference between Set and ArrayList? What are they used for and why have they been created?
- [x] [Master 381] Do you know about HashSet and TreeSet?
- [x] ✅ [Master 249] Can you describe how hashCode() and equals() work together in Collections?
- [x] [Master 396] How does polymorphism benefit the Java Collections Framework?

## Set 20 completion evidence
- docs/sets/SET-020-JAVA-COLLECTIONS.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCollectionSummary.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCollectionSummaryService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectCollectionSummaryServiceTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectCollectionSummaryIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 20 learning items completed: 8 / 8


---

# Set 21

**Status: 21/387+**

- [x] ⭐ [Master 404] What type of collections have you incorporated in your projects?

## Part A
- [x] ✅ [Master 375] What are the major collections we have?
- [x] ✅ [Master 379] What are the main implementations of the List interface?
- [x] [Master 420] In which scenarios is LinkedList preferred over ArrayList?
- [x] [Master 425] In Collections, how does HashSet ensure that there are no duplicates?
- [x] [Master 443] Can you explain how HashMap works in Java?
- [x] [Master 462] Why is HashMap not ordered like LinkedHashMap?
- [x] ✅ [Master 396] How does polymorphism benefit the Java Collections Framework?

## Set 21 completion evidence
- docs/sets/SET-021-COLLECTION-TYPES.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCollectionSummary.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCollectionSummaryService.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectCollectionSummaryServiceTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectCollectionSummaryIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 21 learning items completed: 8 / 8


---

# Set 22

**Status: 22/387+**

- [x] ⭐ [Master 405] Can you tell me a few best practices you consider when applying collections in your project?

## Part A
- [x] ✅ [Master 396] How does polymorphism benefit the Java Collections Framework?
- [x] [Master 551] What are the benefits of using Generics in Java?
- [x] [Master 413] What will happen if you remove an element from an ArrayList while iterating over it using an enhanced for loop?
- [x] [Master 539] Do you know the difference between Fail-Fast and Fail-Safe Iterators?
- [x] [Master 428] What happens if you add a mutable object to a HashSet and then change it?
- [x] [Master 440] What are the issues with using a mutable object as a key in a HashMap?
- [x] [Master 499] How can you design a custom object to be safely used as a key in a HashMap?

## Set 22 completion evidence
- docs/sets/SET-022-COLLECTION-BEST-PRACTICES.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/domain/ProjectIdentity.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectCatalogTest.java
- src/test/java/com/atlasgrid/geoops/project/application/CollectionMutationSafetyTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 22 learning items completed: 8 / 8


---

# Set 23

**Status: 23/387+**

- [x] ⭐ [Master 406] Have you used ArrayList in your project?

## Part A
- [x] ✅ [Master 379] What are the main implementations of the List interface?
- [x] [Master 415] What's the default capacity of an ArrayList?
- [x] [Master 417] How does an ArrayList grow when it exceeds its current capacity?
- [x] [Master 416] You have a List of Integer and call list.remove(1). Does it remove the element at index 1 or the integer value 1?
- [x] ✅ [Master 413] What will happen if you remove an element from an ArrayList while iterating over it using an enhanced for loop?
- [x] ✅ [Master 420] In which scenarios is LinkedList preferred over ArrayList?
- [x] [Master 2136] How is ArrayList different from LinkedList in terms of performance?

## Set 23 completion evidence
- docs/sets/SET-023-ARRAYLIST.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectCatalogTest.java
- src/test/java/com/atlasgrid/geoops/project/application/ArrayListBehaviorTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectIntakePositionIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 23 learning items completed: 8 / 8


---

# Set 24

**Status: 24/387+**

- [x] ⭐ [Master 407] Have you used LinkedList in your project?

## Part A
- [x] ✅ [Master 379] What are the main implementations of the List interface?
- [x] [Master 386] Can you tell me the difference between ArrayList and LinkedList?
- [x] ✅ [Master 420] In which scenarios is LinkedList preferred over ArrayList?
- [x] ✅ [Master 2136] How is ArrayList different from LinkedList in terms of performance?
- [x] [Master 538] What is the difference between Iterator and ListIterator?
- [x] [Master 385] You are given ArrayList, LinkedList and HashSet. Can you tell me when we should use each one and give a real-world example?
- [x] 💡 Why can LinkedList be used as a Deque, and how do addLast(), pollFirst(), and addFirst() map to the GeoOps review workflow?

## Set 24 completion evidence
- docs/sets/SET-024-LINKEDLIST.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewTask.java
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueue.java
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueueController.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueTest.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 24 learning items completed: 8 / 8


---

# Set 25

**Status: 25/387+**

- [x] ⭐ [Master 408] Can you describe a complex problem you solved using a Java Collection?

## Part A
- [x] ✅ [Master 443] Can you explain how HashMap works in Java?
- [x] [Master 400] Can we use a Map and store how many times each element occurs to solve the duplicate-element problem?
- [x] [Master 441] What is the Default Load Factor of a HashMap?
- [x] [Master 446] What happens when two keys have the same hash code?
- [x] [Master 461] Why should we use immutable objects as keys in a Map?
- [x] ✅ [Master 462] Why is HashMap not ordered like LinkedHashMap?
- [x] [Master 464] What's the average lookup time in LinkedHashMap?

## Set 25 completion evidence
- docs/sets/SET-025-COMPLEX-COLLECTION-PROBLEM.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/batch/BatchProjectIntakeEntry.java
- src/main/java/com/atlasgrid/geoops/project/batch/BatchProjectIntakePlan.java
- src/main/java/com/atlasgrid/geoops/project/batch/BatchProjectIntakePlanner.java
- src/main/java/com/atlasgrid/geoops/project/batch/BatchProjectIntakeController.java
- src/test/java/com/atlasgrid/geoops/project/batch/BatchProjectIntakePlannerTest.java
- src/test/java/com/atlasgrid/geoops/project/batch/BatchProjectIntakeControllerIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 25 learning items completed: 8 / 8


---

# Set 26

**Status: 26/387+**

- [x] ⭐ [Master 409] Can you tell me a few Collection names that you are using in your project?

## Part A
- [x] ✅ [Master 375] What are the major collections we have?
- [x] ✅ [Master 379] What are the main implementations of the List interface?
- [x] ✅ [Master 425] In Collections, how does HashSet ensure that there are no duplicates?
- [x] [Master 423] What is the average Lookup Time for a HashSet?
- [x] [Master 424] What is the Load Factor in a HashSet or Hash-based Collection?
- [x] [Master 427] You want to store Custom Objects in a HashSet, but duplicates are being added. What could be wrong in the Object design?
- [x] [Master 434] What's the internal working of HashSet?

## Set 26 completion evidence
- docs/sets/SET-026-COLLECTION-INVENTORY.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectCatalogTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectExistenceLookupIntegrationTest.java
- existing ArrayList, LinkedHashMap and LinkedList collection components
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 26 learning items completed: 8 / 8


---

# Set 27

**Status: 27/387+**

- [x] ⭐ [Master 410] Have you used List, LinkedList and HashSet in your project?

## Part A
- [x] ✅ [Master 375] What are the major collections we have?
- [x] ✅ [Master 379] What are the main implementations of the List interface?
- [x] ✅ [Master 380] What is the difference between Set and ArrayList? What are they used for and why have they been created?
- [x] ✅ [Master 386] Can you tell me the difference between ArrayList and LinkedList?
- [x] ✅ [Master 420] In which scenarios is LinkedList preferred over ArrayList?
- [x] ✅ [Master 425] In Collections, how does HashSet ensure that there are no duplicates?
- [x] ✅ [Master 385] You are given ArrayList, LinkedList and HashSet. Can you tell me when we should use each one and give a real-world example?

## Set 27 completion evidence
- docs/sets/SET-027-LIST-LINKEDLIST-HASHSET.md
- docs/process/SPRINT-004.md
- src/test/java/com/atlasgrid/geoops/project/api/ProjectCollectionStrategyIntegrationTest.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueue.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 27 learning items completed: 8 / 8


---

# Set 28

**Status: 28/387+**

- [x] ⭐ [Master 411] Where have you used List and HashSet? Can you tell me the situations?

## Part A
- [x] ✅ [Master 375] What are the major collections we have?
- [x] ✅ [Master 379] What are the main implementations of the List interface?
- [x] ✅ [Master 380] What is the difference between Set and ArrayList? What are they used for and why have they been created?
- [x] ✅ [Master 425] In Collections, how does HashSet ensure that there are no duplicates?
- [x] ✅ [Master 423] What is the average Lookup Time for a HashSet?
- [x] ✅ [Master 427] You want to store Custom Objects in a HashSet, but duplicates are being added. What could be wrong in the Object design?
- [x] ✅ [Master 396] How does polymorphism benefit the Java Collections Framework?

## Set 28 completion evidence
- docs/sets/SET-028-LIST-HASHSET-SITUATIONS.md
- docs/process/SPRINT-004.md
- src/test/java/com/atlasgrid/geoops/project/application/ProjectCatalogCollectionRoleTest.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/domain/ProjectIdentity.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 28 learning items completed: 8 / 8


---

# Set 29

**Status: 29/387+**

- [x] ⭐ [Master 412] Have you used Arrays.sort() and Collections.sort()?

## Part A
- [x] [Master 395] What's the difference between Arrays.sort() and Collections.sort()?
- [x] [Master 398] Do you know which sorting algorithms Arrays.sort() and Collections.sort() use internally?
- [x] [Master 399] How does Collections.sort() work internally, and which Sorting Algorithm does it use?
- [x] [Master 391] Can you tell me the difference between Comparable and Comparator Interfaces?
- [x] [Master 536] What are Comparator and Comparable used for?
- [x] [Master 537] If a Class implements Comparable but a Custom Comparator is supplied while sorting, which ordering takes precedence?
- [x] [Master 546] Give me a scenario where we should use Comparator.

## Set 29 completion evidence
- docs/sets/SET-029-ARRAYS-COLLECTIONS-SORT.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectSortingView.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectSortingService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectSortingServiceTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectSortingIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 29 learning items completed: 8 / 8


---

# Set 30

**Status: 30/387+**

- [x] ⭐ [Master 419] Have you used ArrayList in your project?

## Part A
- [x] ✅ [Master 379] What are the main implementations of the List interface?
- [x] ✅ [Master 415] What's the default capacity of an ArrayList?
- [x] ✅ [Master 417] How does an ArrayList grow when it exceeds its current capacity?
- [x] ✅ [Master 380] What is the difference between Set and ArrayList? What are they used for and why have they been created?
- [x] [Master 388] In what scenarios would you prefer ArrayList over LinkedList or one over the other?
- [x] [Master 382] You need to store large data, preserve insertion order and perform fast lookups. Which collection would you choose and why?
- [x] ✅ [Master 2136] How is ArrayList different from LinkedList in terms of performance?

## Set 30 completion evidence
- docs/sets/SET-030-ARRAYLIST-RECENT-WINDOW.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectCatalogTest.java
- src/test/java/com/atlasgrid/geoops/project/api/RecentProjectsIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 30 learning items completed: 8 / 8


---

# Set 31

**Status: 31/387+**

- [x] ⭐ [Master 421] Can you tell me the use case of LinkedList in your project?

## Part A
- [x] ✅ [Master 420] In which scenarios is LinkedList preferred over ArrayList?
- [x] ✅ [Master 386] Can you tell me the difference between ArrayList and LinkedList?
- [x] ✅ [Master 2136] How is ArrayList different from LinkedList in terms of performance?
- [x] ✅ [Master 538] What is the difference between Iterator and ListIterator?
- [x] [Master 393] Can you tell me a scenario that causes a ConcurrentModificationException?
- [x] ✅ [Master 413] What will happen if you remove an element from an ArrayList while iterating over it using an enhanced for loop?
- [x] ✅ [Master 539] Do you know the difference between Fail-Fast and Fail-Safe Iterators?

## Set 31 completion evidence
- docs/sets/SET-031-LINKEDLIST-USE-CASE.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueue.java
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueueController.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueTest.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 31 learning items completed: 8 / 8


---

# Set 32

**Status: 32/387+**

- [x] ⭐ [Master 422] Have you used LinkedList in your project?

## Part A
- [x] ✅ [Master 420] In which scenarios is LinkedList preferred over ArrayList?
- [x] ✅ [Master 386] Can you tell me the difference between ArrayList and LinkedList?
- [x] ✅ [Master 2136] How is ArrayList different from LinkedList in terms of performance?
- [x] ✅ [Master 538] What is the difference between Iterator and ListIterator?
- [x] ✅ [Master 393] Can you tell me a scenario that causes a ConcurrentModificationException?
- [x] ✅ [Master 413] What will happen if you remove an element from an ArrayList while iterating over it using an enhanced for loop?
- [x] ✅ [Master 539] Do you know the difference between Fail-Fast and Fail-Safe Iterators?

## Set 32 completion evidence
- docs/sets/SET-032-LINKEDLIST-DEFER.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueue.java
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueueController.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueTest.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 32 learning items completed: 8 / 8


---

# Set 33

**Status: 33/387+**

- [x] ⭐ [Master 437] Have you used TreeSet in your project?

## Part A
- [x] [Master 429] Can you give me an example where you would use HashSet and a scenario where TreeSet is more appropriate?
- [x] [Master 430] Does TreeSet allow null values? Why?
- [x] [Master 432] How does a TreeSet sort Objects internally?
- [x] [Master 541] Do you know about the Comparable Interface?
- [x] [Master 542] Can a Class have multiple Natural Orderings through Comparable?
- [x] [Master 548] If a Comparator returns 0 for two Objects, what happens when those Objects are added to a TreeSet?
- [x] ✅ [Master 391] Can you tell me the difference between Comparable and Comparator Interfaces?

## Set 33 completion evidence
- docs/sets/SET-033-TREESET-CRS-CATALOG.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCrsCatalogService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectCrsCatalogServiceTest.java
- src/test/java/com/atlasgrid/geoops/project/application/TreeSetBehaviorTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectCrsCatalogIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 33 learning items completed: 8 / 8


---

# Set 34

**Status: 34/387+**

- [x] ⭐ [Master 495] Have you worked with HashMap?

## Part A
- [x] ✅ [Master 443] Can you explain how HashMap works in Java?
- [x] ✅ [Master 441] What is the Default Load Factor of a HashMap?
- [x] ✅ [Master 446] What happens when two keys have the same hash code?
- [x] ✅ [Master 461] Why should we use immutable objects as keys in a Map?
- [x] [Master 484] What happens internally when you put a key into a HashMap that already exists?
- [x] [Master 491] What is the time complexity of common HashMap operations such as insertion, deletion and retrieval?
- [x] [Master 492] What is the worst-case time complexity of HashMap if all keys have the same Hash Code?

## Set 34 completion evidence
- docs/sets/SET-034-HASHMAP-CLAIMED-REVIEWS.md
- docs/process/SPRINT-004.md
- docs/maintenance/POST-SET-033-CODEBASE-AUDIT.md
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueue.java
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueueController.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectController.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectQueryController.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectReportController.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueTest.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 34 learning items completed: 8 / 8


---

# Set 35

**Status: 35/387+**

- [x] ⭐ [Master 497] What's the usage of Map in your project?

## Part A
- [x] ✅ [Master 443] Can you explain how HashMap works in Java?
- [x] ✅ [Master 462] Why is HashMap not ordered like LinkedHashMap?
- [x] ✅ [Master 464] What's the average lookup time in LinkedHashMap?
- [x] [Master 465] Can you tell me the internal working of LinkedHashMap?
- [x] ✅ [Master 400] Can we use a Map and store how many times each element occurs to solve the duplicate-element problem?
- [x] ✅ [Master 461] Why should we use immutable objects as keys in a Map?
- [x] ✅ [Master 382] You need to store large data, preserve insertion order and perform fast lookups. Which collection would you choose and why?

## Set 35 completion evidence
- docs/sets/SET-035-MAP-USAGE.md
- docs/process/SPRINT-004.md
- src/test/java/com/atlasgrid/geoops/project/application/ProjectMapUsageIntegrationTest.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCollectionSummaryService.java
- src/main/java/com/atlasgrid/geoops/project/batch/BatchProjectIntakePlanner.java
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueue.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 35 learning items completed: 8 / 8


---

# Set 36

**Status: 36/387+**

- [x] ⭐ [Master 498] Did you get a chance to work with WeakHashMap?

## Part A
- [x] [Master 466] What is a WeakHashMap, and how do Weak References affect its entries?
- [x] ✅ [Master 443] Can you explain how HashMap works in Java?
- [x] ✅ [Master 461] Why should we use immutable objects as keys in a Map?
- [x] ✅ [Master 499] How can you design a custom object to be safely used as a key in a HashMap?
- [x] 💡 Why should WeakHashMap not be used for authoritative business state?

## Set 36 completion evidence
- docs/sets/SET-036-WEAKHASHMAP.md
- docs/architecture/ADR-WEAKHASHMAP-BUSINESS-STATE.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueue.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 36 learning items completed: 6 / 6


---

# Set 37

**Status: 37/387+**

- [x] ⭐ [Master 531] Did you get a chance to work on ConcurrentHashMap in your project?

## Part A
- [x] [Master 505] Can you please brief on ConcurrentHashMap?
- [x] [Master 506] How does ConcurrentHashMap improve performance in a multi-threaded environment?
- [x] [Master 507] How does ConcurrentHashMap handle concurrency differently from HashMap? In what parameters is it different, and how does it handle concurrency?
- [x] [Master 508] You need a collection that supports very frequent reads but occasional writes. What would you choose between HashMap, Collections.synchronizedMap() and ConcurrentHashMap?
- [x] [Master 512] What happens when Two Threads update the Same Key in a ConcurrentHashMap at the same time?
- [x] [Master 517] Is ConcurrentHashMap 100% Thread-Safe for every kind of operation?
- [x] [Master 522] Does ConcurrentHashMap allow null Keys or Values, and why not?

## Set 37 completion evidence
- docs/sets/SET-037-CONCURRENTHASHMAP.md
- docs/architecture/ADR-CONCURRENT-REVIEW-STATE.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/review/ProjectReviewQueue.java
- src/test/java/com/atlasgrid/geoops/review/ProjectReviewQueueTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 37 learning items completed: 8 / 8


---

# Set 38

**Status: 38/387+**

- [x] ⭐ [Master 550] Have you customized sorting before? If yes, for what purpose?

## Part A
- [x] [Master 392] What is the difference between a Sorted Collection and an Ordered Collection?
- [x] ✅ [Master 536] What are Comparator and Comparable used for?
- [x] ✅ [Master 537] If a Class implements Comparable but a Custom Comparator is supplied while sorting, which ordering takes precedence?
- [x] ✅ [Master 541] Do you know about the Comparable Interface?
- [x] [Master 544] Can you tell me the difference between Comparable and Comparator, and which would you use to sort a list of employees by salary?
- [x] ✅ [Master 546] Give me a scenario where we should use Comparator.
- [x] [Master 547] You use a custom Comparator for a TreeMap, but some elements are not getting inserted. What might be wrong?

## Set 38 completion evidence
- docs/sets/SET-038-CUSTOM-SORTING.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectSortingService.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectSortingView.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectReportController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectSortingServiceTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectSortingIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 38 learning items completed: 8 / 8


---

# Set 39

**Status: 39/387+**

- [x] ⭐ [Master 592] What challenges did you face while implementing Functional Interfaces in legacy code?

## Part A
- [x] [Master 564] Can you explain what functional interfaces are?
- [x] [Master 570] Can you please explain the functional interfaces like Predicate, Function and Consumer, and give a real-world use case for each?
- [x] [Master 571] What will happen if you create your own functional interface with two abstract methods?
- [x] [Master 581] How do Lambda Expressions differ from Anonymous Classes?
- [x] [Master 582] Can Lambda Expressions access Non-Final Local Variables, and what happens if you try to modify such a Variable inside the Lambda?
- [x] [Master 588] How is Lambda expression related to Functional Interfaces?
- [x] [Master 590] Can a Lambda Expression throw a Checked Exception?

## Set 39 completion evidence
- docs/sets/SET-039-FUNCTIONAL-INTERFACES-LEGACY.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationCheck.java
- src/main/java/com/atlasgrid/geoops/project/validation/FunctionalProjectValidationRuleAdapter.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationRuleConfiguration.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationRule.java
- src/main/java/com/atlasgrid/geoops/project/validation/CoordinateReferenceSystemValidationRule.java
- src/test/java/com/atlasgrid/geoops/project/validation/ProjectValidationServiceTest.java
- src/test/java/com/atlasgrid/geoops/project/validation/FunctionalProjectValidationRuleAdapterTest.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectServiceTestFactory.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 39 learning items completed: 8 / 8


---

# Set 40

**Status: 40/387+**

- [x] ⭐ [Master 624] Have you worked on Stream APIs?

## Part A
- [x] [Master 595] What are Java Streams?
- [x] [Master 600] What is the difference between filter and map functions of Stream API?
- [x] [Master 601] Do you know about intermediate and terminal operations in Streams?
- [x] [Master 605] How would you use Streams to filter and map a collection of objects?
- [x] [Master 606] Can you explain how Java 8 Stream API enhances collection processing?
- [x] [Master 611] What's the difference between writing code with traditional loops and with Stream API?
- [x] [Master 613] In which scenarios would you avoid Streams and prefer a plain for loop?

## Set 40 completion evidence
- docs/sets/SET-040-STREAM-API.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectDeliverySelection.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectDeliverySelectionService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectReportController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectDeliverySelectionServiceTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectDeliverySelectionIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 40 learning items completed: 8 / 8


---

# Set 41

**Status: 41/387+**

- [x] ⭐ [Master 637] Have you used Optional personally?

## Part A
- [x] [Master 626] What problem does the Optional class solve in Java 8?
- [x] [Master 628] Do you know Optional in Java?
- [x] [Master 630] Besides Null Handling, what other advantages does the Optional class provide?
- [x] [Master 631] How is Optional intended to be used, and how is it commonly misused?
- [x] [Master 632] What is the difference between orElse() and orElseGet()?
- [x] [Master 633] Why can calling Optional.get() be dangerous?
- [x] [Master 635] What is the difference between Optional.of() and Optional.ofNullable()?

## Set 41 completion evidence
- docs/sets/SET-041-OPTIONAL-PERSONAL-USAGE.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectQueryController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectServiceLookupStrategyTest.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectOptionalSemanticsTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectControllerLookupIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 41 learning items completed: 8 / 8


---

# Set 42

**Status: 42/387+**

- [x] ⭐ [Master 638] Do you use Optional practically in your project?

## Part A
- [x] ✅ [Master 626] What problem does the Optional class solve in Java 8?
- [x] [Master 627] Why is returning Optional from a getter method not recommended?
- [x] [Master 629] Why was the Optional class introduced in Java?
- [x] ✅ [Master 631] How is Optional intended to be used, and how is it commonly misused?
- [x] ✅ [Master 632] What is the difference between orElse() and orElseGet()?
- [x] ✅ [Master 633] Why can calling Optional.get() be dangerous?
- [x] ✅ [Master 635] What is the difference between Optional.of() and Optional.ofNullable()?

## Set 42 completion evidence
- docs/sets/SET-042-OPTIONAL-PRACTICAL-USAGE.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectQueryController.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectOptionalPracticalUsageTest.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectIntakePositionIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 42 learning items completed: 8 / 8


---

# Set 43

**Status: 43/387+**

- [x] ⭐ [Master 639] Did you guys leverage the Optional class?

## Part A
- [x] ✅ [Master 626] What problem does the Optional class solve in Java 8?
- [x] ✅ [Master 628] Do you know Optional in Java?
- [x] ✅ [Master 630] Besides Null Handling, what other advantages does the Optional class provide?
- [x] ✅ [Master 631] How is Optional intended to be used, and how is it commonly misused?
- [x] ✅ [Master 632] What is the difference between orElse() and orElseGet()?
- [x] [Master 634] Why is Optional.get() dangerous?
- [x] ✅ [Master 635] What is the difference between Optional.of() and Optional.ofNullable()?

## Set 43 completion evidence
- docs/sets/SET-043-OPTIONAL-BOUNDARIES.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectQueryController.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectOptionalBoundaryIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 43 learning items completed: 8 / 8

---

# Set 44

**Status: 44/387+**

- [x] ⭐ [Master 640] Can you tell me a particular scenario where you used Optional?

## Part A
- [x] ✅ [Master 626] What problem does the Optional class solve in Java 8?
- [x] ✅ [Master 629] Why was the Optional class introduced in Java?
- [x] ✅ [Master 630] Besides Null Handling, what other advantages does the Optional class provide?
- [x] ✅ [Master 631] How is Optional intended to be used, and how is it commonly misused?
- [x] ✅ [Master 632] What is the difference between orElse() and orElseGet()?
- [x] ✅ [Master 633] Why can calling Optional.get() be dangerous?
- [x] ✅ [Master 635] What is the difference between Optional.of() and Optional.ofNullable()?

## Set 44 completion evidence
- docs/sets/SET-044-OPTIONAL-SCENARIO.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectService.java
- src/main/java/com/atlasgrid/geoops/project/api/ProjectQueryController.java
- src/test/java/com/atlasgrid/geoops/project/api/ProjectOptionalBoundaryIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 44 learning items completed: 8 / 8

---

# Set 45

**Status: 45/387+**

- [x] ⭐ [Master 654] Which Java 8 features do you use most of the time?

## Part A
- [x] [Master 650] Why was Java 8 introduced? Why was there a requirement to upgrade from Java 7 to Java 8?
- [x] [Master 651] From Java 8 onwards, what were the major changes introduced in Java 8?
- [x] [Master 580] Why were Lambda Expressions introduced in Java 8?
- [x] ✅ [Master 588] How is Lambda expression related to Functional Interfaces?
- [x] ✅ [Master 606] Can you explain how Java 8 Stream API enhances collection processing?
- [x] ✅ [Master 626] What problem does the Optional class solve in Java 8?
- [x] [Master 642] What changes were introduced to the Date and Time API in Java 8?

## Set 45 completion evidence
- docs/sets/SET-045-JAVA-8-FEATURES.md
- docs/process/SPRINT-004.md
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationRuleConfiguration.java
- src/main/java/com/atlasgrid/geoops/project/validation/ProjectValidationCheck.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectDeliverySelectionService.java
- src/main/java/com/atlasgrid/geoops/project/application/ProjectCatalog.java
- src/main/java/com/atlasgrid/geoops/project/domain/GeoProject.java
- src/test/java/com/atlasgrid/geoops/project/application/ProjectJava8FeatureUsageIntegrationTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 45 learning items completed: 8 / 8

---

# Set 46

**Status: 46/387+**

- [x] ⭐ [Master 655] Which Java version do you use in your current project?

## Part A
- [x] ✅ [Master 14] Can a machine have multiple versions of JDK or JRE installed?
- [x] ✅ [Master 17] Can you tell me the difference between JDK, JRE and JVM?
- [x] [Master 647] Can you tell me some new features that were introduced in Java 17?
- [x] [Master 652] Are you aware of recent Java updates such as Records and Sealed Classes?

## Set 46 completion evidence
- docs/sets/SET-046-JAVA-17-BASELINE.md
- docs/architecture/ADR-JAVA-17-BASELINE.md
- pom.xml
- .java-version
- .github/workflows/ci.yml
- src/main/java/com/atlasgrid/geoops/project/domain/GeoProject.java
- src/test/java/com/atlasgrid/geoops/Java17BaselineTest.java
- docs/ANCHOR_EXPERIENCE_ANSWERS.md

Set 46 learning items completed: 5 / 5

---

Completed job-experience anchors: 46
Synthetic job-experience anchors created so far: 0
Unique master technical questions covered: 203
Synthetic technical questions covered: 10
Current denominator: 387+
