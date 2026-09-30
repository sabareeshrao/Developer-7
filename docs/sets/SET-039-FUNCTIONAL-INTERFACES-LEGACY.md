# Set 39 — Functional Interfaces in Legacy Validation Code

**Status:** 39/387+  
**Anchor:** ⭐ What challenges did you face while implementing Functional Interfaces in legacy code?

## Project implementation

GeoOps already had this legacy validation contract:

~~~java
public interface ProjectValidationRule {
    String code();
    List<ValidationIssue> validate(CreateProjectRequest request);
}
~~~

It is useful for polymorphism, but it is not a Functional Interface because it has two abstract methods.

Set 39 therefore introduces an incremental migration seam instead of breaking every existing rule:

~~~text
ProjectValidationCheck
@FunctionalInterface
        ↓ lambda
FunctionalProjectValidationRuleAdapter
        ↓
existing ProjectValidationRule
        ↓
ProjectValidationService
~~~

The project-code rule is migrated to a lambda-backed Spring bean. The CRS rule remains the existing class-based implementation, proving both styles can coexist.

---

## Part A

### Can you explain what Functional Interfaces are?

A Functional Interface has exactly one abstract method. It can therefore be the target type of a lambda expression.

GeoOps adds:

~~~java
@FunctionalInterface
public interface ProjectValidationCheck {
    List<ValidationIssue> validate(CreateProjectRequest request);
}
~~~

### Can you please explain the functional interfaces like Predicate, Function and Consumer, and give a real-world use case for each?

`Predicate<T>` represents T → boolean, such as checking whether a request satisfies a rule.

`Function<T,R>` represents T → R, such as converting a GeoProject to its project code.

`Consumer<T>` represents T → void, such as sending a ValidationIssue to logging or auditing.

GeoOps uses a custom functional interface because validation returns a List<ValidationIssue>.

### What will happen if you create your own Functional Interface with two abstract methods?

It is no longer a valid Functional Interface. If it is annotated with @FunctionalInterface, the compiler reports an error.

That is exactly why the existing ProjectValidationRule could not simply become the lambda target: it already had code() and validate().

### How do Lambda Expressions differ from Anonymous Classes?

A lambda is concise and targets a Functional Interface. It does not introduce a new `this` scope and captures surrounding variables under effectively-final rules.

An anonymous class creates an anonymous class instance and has its own `this`.

For the stateless project-code validation behavior, the lambda form is a good fit.

### Can Lambda Expressions access Non-Final Local Variables, and what happens if you try to modify such a Variable inside the Lambda?

A lambda can capture a local variable only if it is final or effectively final.

Set 39 captures:

~~~java
String ruleCode = "PROJECT_CODE_FORMAT";
String example = ProjectValidationStandards.PROJECT_CODE_EXAMPLE;
~~~

Neither is reassigned. Reassigning one would make the lambda fail to compile.

### How is Lambda expression related to Functional Interfaces?

A lambda needs a target Functional Interface so the compiler knows the parameter and return contract.

GeoOps flow:

~~~text
request -> List<ValidationIssue>
        ↓ target type
ProjectValidationCheck
        ↓ adapter
ProjectValidationRule
~~~

### Can a Lambda Expression throw a Checked Exception?

Only if the target Functional Interface method declares that checked exception, or the lambda handles/wraps it.

ProjectValidationCheck.validate(...) does not declare checked exceptions, so a checked-exception legacy method could not be migrated unchanged.

Possible strategies are handling it inside the lambda, wrapping it appropriately, or defining another Functional Interface whose method declares the exception.

---

## Legacy migration challenge found by CI

The first complete migration failed because an older test factory still directly instantiated the removed concrete ProjectCodeValidationRule.

That hidden construction site had to be migrated to:

~~~java
new ProjectValidationRuleConfiguration()
        .projectCodeValidationRule()
~~~

This is a realistic legacy-code challenge: application code may depend on interfaces while old tests or helper factories still depend directly on concrete implementations.

After that fixture was updated, the full Maven verification passed.

## Why use an adapter?

Without the adapter, converting the old two-method interface into a Functional Interface would require changing all implementations and consumers together.

The adapter keeps metadata and functional behavior separate:

~~~text
legacy metadata: code()
+
functional behavior: validate()
~~~

That gives GeoOps a gradual migration path.

## Set 39 world decision

- ProjectValidationRule remains the legacy two-method service contract.
- ProjectValidationCheck is the new lambda-compatible @FunctionalInterface.
- FunctionalProjectValidationRuleAdapter bridges new behavior into the old contract.
- Project-code validation is now a lambda-backed Spring bean.
- CRS validation remains class-based.
- Lambdas capture only final/effectively-final locals.
- Checked exceptions require an explicit migration strategy.
- Set 39 adds seven new master technical questions.
- Existing validation behavior is preserved.

---

## Experience Answer

Yes. One challenge I faced in GeoOps was that the existing validation interface was not lambda-compatible. It exposed both code() and validate(), so it had two abstract methods and could not simply be marked as a Functional Interface without breaking the existing Spring validation design.

I handled that incrementally. I introduced a one-method @FunctionalInterface called ProjectValidationCheck and an adapter that converts the lambda-compatible check back into the existing ProjectValidationRule contract. I migrated only the project-code rule to a lambda-backed Spring bean while the CRS rule stayed as the legacy class. We also found an older test factory that directly instantiated the removed concrete rule, which showed the hidden coupling you often uncover during legacy refactoring. I also had to account for effectively-final variable capture and checked-exception compatibility when choosing the functional method signature.