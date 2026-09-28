# World Canon

## Status

This is a **fictional interview-simulation world**. It is designed to create internally consistent, technically realistic job-experience scenarios. It is not a record of factual employment events.

## Fixed premise

**Fictional company:** AtlasGrid Geospatial Systems  
**Business domain:** geographic information, surveying, mapping-data processing, geospatial quality control, and client delivery  
**Core enterprise product:** GeoOps — a platform that coordinates geospatial project intake, data/file processing, validation, workflow status, quality review, and customer delivery.

## Initial workflow

```text
Client / Survey Data
        ↓
Project Intake
        ↓
Inbound File & Metadata Validation
        ↓
Geospatial Processing Workflow
        ↓
Quality Review
        ↓
Delivery Packaging
        ↓
Customer / Downstream Systems
```

## Grounding boundaries

The fictional premise is inspired by the supplied resume's real technology/domain themes: Java backend development, Spring Boot services, REST/SOAP integrations, geospatial project intake, survey-data processing, file tracking, validation/transformation, scheduled jobs, database workflows, production support, Jenkins/Git/Linux tooling, and testing.

Specific fictional incidents, metrics, architecture decisions, team names, service names, client situations, and implementation stories are established only as the learning sequence and GIS codebase require them.

## Canon rules

1. Never contradict an already-established fact.
2. Prefer extending an existing component over inventing a duplicate.
3. Do not claim use of a technology until a question or codebase requirement establishes or reasonably requires it.
4. If a later question forces a change, record the evolution explicitly (for example: monolith → microservices or Java 8 → 17).
5. Keep project stories technically plausible and connected to the GIS workflow.
6. Separate **fictional interview simulation** from factual resume history.
7. Every processed question gets a world record containing:
   - question
   - source type: original or synthetic
   - 80/20 concept
   - interview-ready answer
   - world facts introduced
   - code/data/architecture changes if any
   - dependencies on earlier questions
   - future hooks created
8. If the GIS codebase produces an important responsibility, architecture decision, production scenario, data challenge, performance issue, security concern, or operational task that has no suitable question in the original bank, create a new **synthetic GIS job-experience anchor**.
9. Synthetic anchors must be explicitly labeled and must never be presented as part of the original 387-question source bank.
10. Do not create synthetic anchors for trivial details; create them only when they strengthen the coherent job-experience world or fill a genuine coverage gap.
