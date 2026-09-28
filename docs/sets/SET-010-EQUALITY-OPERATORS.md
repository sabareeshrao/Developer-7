# Set 10 — == and .equals() in GeoOps

**Status:** 10/387+  
**Anchor:** ⭐ Have you used == and .equals() operators in your project?

## Project implementation

Yes. GeoOps now has two explicit equality use cases:

```text
Reference identity
    ↓
==

Logical/content equality
    ↓
.equals()
```

Inside `ProjectIdentity.equals(...)`, GeoOps uses:

```java
if (this == other) {
    return true;
}
```

That is a valid reference-identity fast path because if both references point to the exact same object, the equality method can immediately return true.

For project-code lookup, GeoOps uses:

```java
project.projectCode().equals(projectCode)
```

because an incoming path/request string can be a different String object containing the same business value.

The new endpoint is:

```text
GET /api/projects/exists/{projectCode}
```

---

## Part A

### ✅ 1. How would you handle a situation where you need to compare the content equality of two custom object instances? — Master 246

Already covered in Set 9.

For custom objects, GeoOps defines logical equality explicitly through `ProjectIdentity.equals(...)`.

The Set 10 distinction is that object-content equality and reference identity are different questions.

### 2. What's the difference between == and .equals() in Java? — Master 262

For object references:

- `==` checks whether two references point to the same object.
- `.equals()` checks logical equality according to that class's implementation.

GeoOps demonstrates both:

```java
this == other
```

checks whether two `ProjectIdentity` references are literally the same object.

But:

```java
project.projectCode().equals(projectCode)
```

checks whether two String objects contain the same project-code text.

For primitives, `==` compares primitive values directly.

### 3. How does the equals() method in String work, and how is it different from the == operator? — Master 266

`String.equals(...)` compares the character content of the strings.

`==` compares the references.

The Set 10 test intentionally creates:

```java
String requestProjectCode = new String("TX-AUS-001");
```

That produces a different String object.

Therefore:

```java
requestProjectCode == "TX-AUS-001"
```

is false in the test, while:

```java
requestProjectCode.equals("TX-AUS-001")
```

is true.

GeoOps therefore uses `.equals()` for project-code business comparison.

### 4. In a user-authentication module, what issues could arise if we use == instead of equals() to compare credentials? — Master 267

Using `==` for String credentials can reject equal text simply because the two values were created as different String objects.

The same category of bug applies to GeoOps project codes.

A project code arriving from an HTTP request should be compared by its content, not by whether its String reference happens to be the same object as a stored String.

For credentials specifically, real authentication systems should also use appropriate secure password-verification mechanisms rather than plain String equality for passwords; the equality lesson here is about why `==` is not content comparison.

### 5. Two Integer variables, A and B, both contain 200. What will A == B return, true or false? — Master 302

For ordinary autoboxing on a standard JVM:

```java
Integer a = 200;
Integer b = 200;
```

`a == b` is typically false because 200 is outside Java's required Integer cache range.

The objects can still be value-equal:

```java
a.equals(b) // true
```

This reinforces the rule that wrapper-object business values should not be compared using reference identity.

### 6. Let's say Integer A = 127 and Integer B = 127. If you do A == B, will it return true or false? — Master 2500

With normal Java autoboxing:

```java
Integer a = 127;
Integer b = 127;
```

`a == b` is true because Java requires Integer values from -128 through 127 to be cached for this form of boxing.

This can make `==` appear to work for wrapper values and lead to misleading code.

For value comparison, use `.equals()` or unbox to primitives when appropriate.

### 7. If Integer C = 128 and Integer D = 128, will C == D print true or false? — Master 2501

On a normal/default JVM:

```java
Integer c = 128;
Integer d = 128;
```

`c == d` is typically false because 128 lies outside the required -128 to 127 cache range.

But:

```java
c.equals(d)
```

is true.

The interview lesson is not to use wrapper-reference caching behavior as a substitute for value comparison.

---

## GeoOps comparison flow

```text
Incoming project code
        ↓
different String object possible
        ↓
ProjectService.containsProjectCode(...)
        ↓
storedCode.equals(incomingCode)
        ↓
business-value match
```

The service does not use:

```java
storedCode == incomingCode
```

because that would make project lookup depend on JVM object identity rather than project-code value.

## Existing == use

`ProjectIdentity.equals(...)` contains:

```java
if (this == other) {
    return true;
}
```

This is intentional.

`==` is correct there because the code is explicitly asking:

> Are these two references the exact same ProjectIdentity object?

If not, the method moves on to logical value comparison.

## Tests

`ProjectServiceEqualityOperatorTest` proves:

```text
new String("TX-AUS-001")
        ↓
different reference
        ↓
== literal → false
.equals(literal) → true
        ↓
GeoOps lookup → true
```

It also verifies that a different project code does not match.

## Set 10 world decision

- Project-code business comparisons use `String.equals(...)`.
- `==` is reserved for actual reference-identity checks, such as the fast path inside `ProjectIdentity.equals(...)`.
- GeoOps exposes `GET /api/projects/exists/{projectCode}`.
- HTTP-provided String values must not depend on String-pool/reference behavior.
- Wrapper caching is treated as a Java implementation/runtime behavior to understand, not a business-equality mechanism.
- No database lookup has been introduced yet; project-code existence is still checked against the in-memory service state.

---

## Experience Answer

Yes, I have used both `==` and `.equals()` in the GeoOps project, but for different purposes. In our `ProjectIdentity.equals()` implementation, I use `this == other` as a quick check to see whether both references point to the exact same object.

For business-value comparison, such as matching a project code coming from an API request against a stored project code, I use `.equals()`. An HTTP value can be a completely different String object even when it contains the same text, so using `==` there could incorrectly report that the project does not exist. We added a project-code existence lookup and a test with a separately created `String` to prove that reference equality can be false while content equality is true. So in the project I use `==` for identity and `.equals()` for logical/value comparison.
