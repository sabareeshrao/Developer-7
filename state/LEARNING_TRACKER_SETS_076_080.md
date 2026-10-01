# Learning Tracker Supplement — Sets 76–80

This is a continuation of `state/LEARNING_TRACKER.md`. Original 387-anchor count is unchanged.

## Set 76 — 76/387+

- [x] ⭐ [Master 845] In your current project, did you write any Multithreaded code?

### Part A
- [x] ✅ [Master 786] Can you brief on what multithreaded applications do and why we use a multithreaded environment?
- [x] ✅ [Master 913] Why is ExecutorService required?
- [x] [Master 785] Why are immutable objects useful for concurrent programming?
- [x] [Master 792] Do you know about threads? Can you explain them?
- [x] [Master 796] What happens if start() is called more than once on the same Thread?
- [x] [Master 802] Can you explain the lifecycle of a Thread?
- [x] [Master 778] What are the potential issues when directly extending the Thread class instead of implementing Runnable for a high-load application?

Evidence: `docs/sets/SET-076-MULTITHREADED-CODE.md`

## Set 77 — 77/387+

- [x] ⭐ [Master 846] Do you have any concurrent system in your project?

### Part A
- [x] ✅ [Master 880] Do you know about Thread Safety?
- [x] ✅ [Master 850] What is a thread-safe class? Can you name a few from Java?
- [x] ✅ [Master 505] Can you please brief on ConcurrentHashMap?
- [x] ✅ [Master 517] Is ConcurrentHashMap 100% Thread-Safe for every kind of operation?
- [x] ✅ [Master 881] How can you ensure a method is thread-safe in Java?
- [x] [Master 886] Have you heard about AtomicInteger and atomic values?
- [x] [Master 889] How do Atomic Classes such as AtomicInteger differ from synchronized blocks in terms of Performance and Usage?

Evidence: `docs/sets/SET-077-CONCURRENT-SYSTEM.md`

## Set 78 — 78/387+

- [x] ⭐ [Master 847] Have you faced any Concurrency bug, and how did you find and fix it?

### Part A
- [x] ✅ [Master 864] Can you describe a scenario where not using synchronized could cause an issue?
- [x] ✅ [Master 797] How would you handle a scenario where two threads need to update the same data structure?
- [x] ✅ [Master 881] How can you ensure a method is thread-safe in Java?
- [x] [Master 848] You receive high traffic on an API that updates a shared counter. How would you make it thread-safe and performant?
- [x] [Master 890] What's the difference between making variables atomic and making a method synchronized?
- [x] ✅ [Master 889] How do Atomic Classes such as AtomicInteger differ from synchronized blocks in terms of Performance and Usage?
- [x] ✅ [Master 920] Your Service uses a fixed Thread Pool of size 10. Traffic suddenly spikes and requests queue heavily. How would you tune the Thread Pool?

Evidence: `docs/sets/SET-078-CONCURRENCY-BUG.md`

## Set 79 — 79/387+

- [x] ⭐ [Master 910] Have you used the volatile keyword in any of your projects?

### Part A
- [x] [Master 891] Do you know about the volatile keyword?
- [x] [Master 893] If a variable is declared volatile, is it guaranteed that operations on it are atomic?
- [x] [Master 894] Does the volatile keyword resolve a concurrency problem, or is it just used for visibility issues?
- [x] [Master 895] Can volatile alone ensure thread safety?
- [x] [Master 896] Can the volatile keyword solve concurrency issues?
- [x] [Master 898] Can volatile be helpful for resolving concurrency issues when multiple Threads are accessing a variable?
- [x] [Master 907] How does the Java Memory Model affect the visibility of changes made by one thread to another?

Evidence: `docs/sets/SET-079-VOLATILE.md`

## Set 80 — 80/387+

- [x] ⭐ [Master 926] Can you tell me about Thread Pool? Why do we use it, and have you used Thread Pool concepts in any personal or professional project?

### Part A
- [x] ✅ [Master 919] Do you know about Thread Pool?
- [x] ✅ [Master 920] Your Service uses a fixed Thread Pool of size 10. Traffic suddenly spikes and requests queue heavily. How would you tune the Thread Pool?
- [x] ✅ [Master 921] What different types of Thread Pools are available, such as Fixed, Cached and Scheduled Thread Pools?
- [x] ✅ [Master 922] What is the difference between ThreadPoolExecutor and ForkJoinPool?
- [x] ✅ [Master 923] You need to process 10,000 independent tasks but want to limit execution to 20 concurrent tasks at a time. How would you implement this?
- [x] [Master 924] Which Thread Pool does a Parallel Stream use internally?
- [x] [Master 911] What is ForkJoinPool, and how is it related to Parallel Streams?

Evidence: `docs/sets/SET-080-THREAD-POOL.md`

---

**Checkpoint:** 80/387+ · 80 original anchors complete · 307 original anchors remain · 329 unique master technical questions · 32 synthetic technical questions · 0 synthetic job anchors.
