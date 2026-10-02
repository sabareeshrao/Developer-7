# GeoOps Experience Answers — Sets 81–85

These are fictional development examples and controlled labs, not verified personal employment incidents.

## Set 81 — 81/387+

⭐ **Have you ever faced a Deadlock situation in your project?**

In the fictional GeoOps development environment, I reproduced a deadlock in an isolated JVM to understand what happens when two threads acquire the same two locks in opposite order. I used `ThreadMXBean.findDeadlockedThreads()` and thread-dump evidence to identify which thread was waiting for which lock. In the actual GeoOps intake workflow, I prevent that cycle by maintaining a consistent order across the intake monitor, catalog monitor and review ReentrantLock. This is a controlled lab, not a claim that we experienced a customer production deadlock.

## Set 82 — 82/387+

⭐ **Have you worked with ThreadLocal?**

Yes. In the fictional GeoOps project I used `ThreadLocal` to provide a temporary project-code trace context to the validation rules executing on reusable worker threads. I set the value at the worker-task boundary and remove it in a `finally` block, even when a validation rule throws. I added tests that reuse the same executor thread and verify no project code remains afterward. This matters because a thread pool keeps threads alive beyond individual requests; failing to remove ThreadLocal values can retain objects or expose stale request context to later tasks.

## Set 83 — 83/387+

⭐ **Are you guys following SOLID principles?**

Yes. In the fictional GeoOps codebase we follow SOLID where it improves maintainability. The clearest examples are our GIS validation rules: each rule handles one concern, the orchestrator depends on the small `ProjectValidationRule` interface, and new rule implementations can be injected without modifying the orchestration loop. I have tests showing a custom rule can be substituted and composed with existing rules. I do not introduce interfaces for every tiny class when there is no extension or testing benefit.

## Set 84 — 84/387+

⭐ **Can you give me an example where you have applied a SOLID principle in your code?**

Yes. In the fictional GeoOps project I used the Open/Closed and Single Responsibility principles to introduce a project-name-length rule. I created a new class implementing the existing `ProjectValidationRule` interface and annotated it as a Spring component. The existing `ProjectValidationService` needed no modifications because it depends on the rule interface and composes all registered implementations. A Spring integration test proves that names over 120 characters are rejected while names at the boundary remain valid.

## Set 85 — 85/387+

⭐ **Which SOLID principle do you use most in your projects?**

In the fictional GeoOps codebase I apply the Single Responsibility Principle most often. For example, the validation service only coordinates rules, while each rule handles one project-intake concern. For the new project-name length check, I split the pure length policy from the rule that maps policy failure into a typed validation issue. Now changing the length threshold does not require editing the Spring wiring or aggregation logic, and changing the error representation does not require changing the threshold calculation. I use SOLID pragmatically rather than creating extra layers with no clear responsibility boundary.

