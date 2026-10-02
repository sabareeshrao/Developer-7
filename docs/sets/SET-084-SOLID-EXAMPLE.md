# Set 84 — Concrete SOLID example

**Status:** 84/387+  
**Anchor:** ⭐ Can you give me an example where you have applied a SOLID principle in your code?

## Part A

- ✅ [Master 1025] Can you explain the Single Responsibility Principle?
- [Master 1015] Give me an example of a design pattern which is based on the Open/Closed Principle.
- [Master 1016] A Payment Module supports Razorpay, PayPal and Stripe, with new Providers expected every quarter. How would you design it so new Providers can be added without major changes?
- [Master 1018] Do you know about composition over inheritance principle?
- [Master 1022] How does Spring help achieve the Open-Closed Principle?
- [Master 1030] A class is doing database logic, email and validation. Which SOLID principle is violated?
- [Master 1032] What's the similarity between Strategy pattern and the Open/Closed Principle from SOLID?

## Actual code change

GeoOps adds `ProjectNameLengthValidationRule`, a separate Spring `@Component` implementing the existing `ProjectValidationRule` contract. It rejects GIS project display names longer than 120 characters with a typed `PROJECT_NAME_LENGTH` issue.

There were **zero changes to `ProjectValidationService`**: Spring discovers the new rule and the existing orchestrator invokes it polymorphically. The integration test verifies that Spring really injects the rule and the length boundary is accepted at 120. That demonstrates **Open/Closed**, **Single Responsibility**, **Dependency Inversion**, and interface-based composition. The payment-provider question is a *technical analogy*, not a claim GeoOps has payment integration.

## Experience Answer

Yes. In the fictional GeoOps project I used the Open/Closed and Single Responsibility principles to introduce a project-name-length rule. I created a new class implementing the existing `ProjectValidationRule` interface and annotated it as a Spring component. The existing `ProjectValidationService` needed no modifications because it depends on the rule interface and composes all registered implementations. A Spring integration test proves that names over 120 characters are rejected while names at the boundary remain valid.
