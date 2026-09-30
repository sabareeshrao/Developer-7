# Developer-7 / GeoOps — New Chat Handover After Set 55

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch: `main`.

Do not rely on old chat memory. GitHub is the source of truth.

## VERIFIED LEARNING CHECKPOINT

```text
Completed Sets:                         55
Completed original job anchors:        55
Original anchor pool:                 387
Original anchors remaining:           332
Synthetic ⭐⭐ job anchors:               0
Current denominator:                  387+
Unique master technical questions:    231
Synthetic technical 💡 questions:      15
Current Status:                    55/387+
```

Latest verified executable Set-55 checkpoint:

`8376032f9dcd76f1876a79f42dcc3d61d7fb8113`

That code checkpoint is green under `mvn clean verify` including tests and SpotBugs. A later documentation/state commit may exist; inspect current HEAD and let newer repository state win.

## CURRENT BASELINE

```text
Java 17
Spring Boot 4.1.1
Maven
Apache Commons CSV 1.14.1
Spring MVC / Jackson 3 JSON
Bean Validation
Actuator
Lombok
JUnit
SpotBugs
GitHub Actions
```

## SETS 51–55

```text
Set 51 → strict JSON deserialization
Set 52 → trusted Java snapshot serialization
Set 53 → JSON / CSV / native serialization boundaries
Set 54 → typed snapshot failure handling
Set 55 → real ClassNotFoundException regression + classpath/ClassLoader policy
```

Important boundaries:
- CSV remains Apache Commons CSV, not ObjectInputStream.
- REST JSON uses Spring/Jackson.
- Native Java serialization is only the trusted internal snapshot utility.
- GeoProject itself remains non-Serializable.
- ObjectInputStream uses an allowlist filter and resource limits.
- database persistence/PostgreSQL/PostGIS/auth/Kafka/Redis/Docker/Kubernetes remain deferred unless a future anchor introduces them.

## NEXT REQUIRED SET

### Set 56 — Status: 56/387+

⭐ **Have you used reflection somewhere in your project?**

This is the next exact original experience anchor. Do not skip it or combine another ⭐/⭐⭐ anchor into the same Set.

Before Set 56:
1. inspect latest 10–20 commits;
2. read CONTINUATION_PROTOCOL.md;
3. read state/progress.json;
4. read state/LEARNING_TRACKER.md;
5. read world/CANON.md;
6. read AI_CONTEXT.md;
7. read docs/sets/SET-055-CLASSNOTFOUND-DESERIALIZATION.md;
8. inspect current snapshot/ClassLoader code and tests;
9. search the 2,308 master bank for Reflection questions;
10. check CI;
11. reuse prior technical questions with ✅;
12. end with ## Experience Answer.

Permanent one-anchor/marker/7-per-Part rules remain unchanged.
