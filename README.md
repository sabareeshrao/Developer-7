# Developer-7 — GIS Job Experience World

This repository is a **fictional interview-simulation world** built progressively around a geographic / GIS enterprise.

## Purpose

The goal is to build one internally consistent GIS job-experience world while learning the technical knowledge in a **natural ground-zero sequence**.

The source banks contain:
- **387 job-experience questions**
- **2,308 total interview questions**

The 387 job-experience questions are the initial **anchors**. The larger bank supplies the related technical questions that explain each anchor from fundamentals upward.

## Learning sequence

We do **not** simply walk through all 387 job-experience questions in source order.

For each job-experience anchor:

1. Start with the job-experience question.
2. Identify the minimum prerequisite concepts needed to understand it.
3. Pull in related non-job technical questions from the larger interview bank.
4. Arrange those technical questions from **ground zero → practical understanding → project implementation**.
5. Apply the knowledge inside the same fictional GIS world.
6. Persist any new world facts, architecture, code, data models, incidents, or processes.
7. If the GIS codebase introduces an important real-world responsibility or scenario that has **no matching job-experience question**, create a new **synthetic GIS job-experience anchor** for it.
8. Only then move to the next logical anchor.

Conceptually:

```text
Job Experience Anchor
        ↓
Ground-Zero Prerequisites
        ↓
Related Technical Questions
        ↓
Java / Spring / Database / DevOps Understanding
        ↓
GIS Project Implementation
        ↓
Synthetic GIS Job-Experience Anchor if needed
        ↓
Interview-Ready Experience Story
        ↓
Next Logical Anchor
```

## Synthetic-anchor rule

New synthetic job-experience questions are allowed when the evolving GIS world requires experience that the original 387-question bank does not cover.

They must:
- be clearly marked as **synthetic / generated**
- be technically justified by the GIS codebase
- reuse existing world canon
- avoid duplicating an existing source question
- be inserted where they naturally belong in the learning flow
- never alter the numbering or wording of the original 387 questions

Example:

If we build a PostGIS spatial-query feature but no source question asks about spatial indexing, we can add a synthetic experience question such as:

> How did you improve the performance of spatial queries in your GIS application?

That new question becomes part of the fictional world while the original question bank remains unchanged.

## Chat contract

For every study question, the chat answer follows this learning intent:

> I want to learn about [question].  
> "Show me only the most important 20% that will help me understand the other 80%."

The explanation should be concise, practical, and connected to the evolving GIS world whenever relevant.

## World continuity

The fictional company is **AtlasGrid Geospatial Systems** and the core platform is **GeoOps**.

No later answer may casually contradict established world canon. New technologies, services, incidents, and design decisions are introduced only when the learning sequence needs them.

## Grounding

The fictional world is inspired by the supplied resume's Java/Spring Boot and geospatial-platform background, but repository scenarios are simulation material and should not be represented as factual employment history.

## Current state

- Repository initialized
- 387 source job-experience questions loaded
- Master bank count recorded: 2,308
- Synthetic GIS job-experience anchors: allowed when needed
- Learning mode: **natural topic clusters**
- Current anchor: **Job Experience Question 1**
- Current phase: **ground-zero sequencing before progressing**
