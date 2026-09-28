# Question Banks

## Source banks

### Job-experience anchors
The repository contains the dedicated **387-question Job Experience bank**:

- [Questions 001–100](job-experience-001-100.md)
- [Questions 101–200](job-experience-101-200.md)
- [Questions 201–300](job-experience-201-300.md)
- [Questions 301–387](job-experience-301-387.md)

### Master interview bank
The supplied workbook contains **2,308 total interview questions** spanning Java, Spring, testing, databases, architecture, DevOps, cloud, production support, leadership, and related areas.

## Sequencing rule

The source numbering stays immutable, but **learning order is logical rather than flat**.

A job-experience question acts as an **anchor**. Before moving to the next anchor, the system may study multiple related non-job technical questions from the larger bank.

The ordering rule is:

```text
Anchor Job-Experience Question
→ prerequisite concepts
→ basic technical questions
→ intermediate technical questions
→ project-level technical questions
→ GIS implementation/story
→ next logical anchor
```

## Ground-zero rule

When a technical topic appears, begin from the lowest useful prerequisite that makes the anchor understandable. Do not assume advanced knowledge merely because the interview question is advanced.

## Brain-sync rule

Questions should be grouped by **concept continuity**. Avoid jumping from unrelated topics just because their source numbers are adjacent.

Examples:
- Development environment anchor → IDE → JDK → Maven → Spring Boot project structure → debugging/build workflow.
- Data import/export anchor → files/data formats → serialization/deserialization → validation → batch processing → database persistence → GIS ingestion workflow.
- Multithreading anchor → process/thread basics → executors/thread pools → synchronization → concurrent collections → async GIS processing.
- Production issue anchor → logging → metrics → JVM/database/API diagnosis → deployment environment → incident/RCA story.

## Advancement

When the user says **Next**, continue from the current logical position:
- next technical prerequisite if the current anchor is not yet complete;
- otherwise the next related technical question;
- once the anchor cluster is complete, advance to the next logical job-experience anchor.

The system processes **one learning item at a time** unless the user explicitly requests more.
