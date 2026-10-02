# Set 82 — ThreadLocal and worker reuse

**Status:** 82/387+  
**Anchor:** ⭐ Have you worked with ThreadLocal?

## Part A

- [Master 950] You want each user session in a multithreaded application to maintain its own instance of a variable. What Java concept would you use?
- [Master 952] Do you know about ThreadLocal?
- [Master 826] Can you describe a situation where using ThreadLocal can introduce Memory Leaks?
- ✅ [Master 916] How would you cancel a long-running Callable?
- ✅ [Master 785] Why are immutable objects useful for concurrent programming?
- ✅ [Master 907] How does the Java Memory Model affect the visibility of changes made by one thread to another?
- ✅ [Master 880] Do you know about Thread Safety?

## Implementation

GeoOps has a bounded reusable worker pool. `ValidationTraceContext` carries only the current GIS project code in a worker-confined `ThreadLocal` while one request is being validated. `ParallelProjectValidationService.validateOne` uses a scope helper that sets the code, evaluates the rules, and **always calls `remove()` in `finally`**. The code is not treated as persistent business state or request authentication.

`ValidationTraceContextTest` proves both that successive tasks on one worker see their own project codes and that the worker contains no stale value after success **or exception**. The same context is not silently inherited by newly spawned threads.

## Experience Answer

Yes. In the fictional GeoOps project I used `ThreadLocal` to provide a temporary project-code trace context to the validation rules executing on reusable worker threads. I set the value at the worker-task boundary and remove it in a `finally` block, even when a validation rule throws. I added tests that reuse the same executor thread and verify no project code remains afterward. This matters because a thread pool keeps threads alive beyond individual requests; failing to remove ThreadLocal values can retain objects or expose stale request context to later tasks.
