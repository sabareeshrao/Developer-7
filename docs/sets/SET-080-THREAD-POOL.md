# Set 80 — Thread Pool usage and limits

**Status:** 80/387+  
**Anchor:** ⭐ Can you tell me about Thread Pool? Why do we use it, and have you used Thread Pool concepts in any personal or professional project?

## Part A
- ✅ [Master 919] Do you know about Thread Pool?
- ✅ [Master 920] Your Service uses a fixed Thread Pool of size 10. Traffic suddenly spikes and requests queue heavily. How would you tune the Thread Pool?
- ✅ [Master 921] What different types of Thread Pools are available, such as Fixed, Cached and Scheduled Thread Pools?
- ✅ [Master 922] What is the difference between ThreadPoolExecutor and ForkJoinPool?
- ✅ [Master 923] You need to process 10,000 independent tasks but want to limit execution to 20 concurrent tasks at a time. How would you implement this?
- [Master 924] Which Thread Pool does a Parallel Stream use internally?
- [Master 911] What is ForkJoinPool, and how is it related to Parallel Streams?

## Implementation

GeoOps's bounded `ThreadPoolExecutor` has four worker threads and a 64-task queue. Saturation runs tasks in the caller, slowing producers; shutdown rejects new work. `ProjectValidationThreadPoolWorkloadTest` holds all workers, fills the queue, proves caller execution, and checks clean pool termination. Thread pools reuse threads to avoid one OS thread per input; `ForkJoinPool` and parallel streams use different work-stealing semantics and are not substituted into this ordered GIS validation API.

## Experience Answer

Yes. In the fictional GeoOps application, I configured a Spring-managed `ThreadPoolExecutor` for parallel GIS intake validation. It has four reusable workers, a 64-slot bounded queue and caller-runs backpressure while active. I added a regression that holds all four workers, fills all 64 queue slots and proves the next task runs on the submitting thread. A separate Set-78 fix rejects work after shutdown instead of silently discarding it. The service aggregates Futures in input order and cancels outstanding tasks when failures occur; these controls do not make the in-memory system distributed.
