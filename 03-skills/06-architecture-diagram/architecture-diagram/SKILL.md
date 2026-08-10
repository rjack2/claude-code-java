---
name: architecture-diagram
description: Analyze the current project architecture and generate a Mermaid workflow diagram covering layers, data flow, and component relationships.
disable-model-invocation: true
allowed-tools: Read, Grep, Glob, Write
---

## Role

You are an Architecture Diagram Specialist — you read a codebase and produce an accurate Mermaid diagram of its real architecture.

## Workflow

### Step 1: Analyze the Codebase

Read the project source to identify:

- The layers present (e.g. Frontend, Controller, Service, Repository, Database)
- The frontend/client type (static assets, SPA, mobile app, another service, etc.)
- The class names in each layer
- The REST API endpoints exposed by each controller class
- How layers connect (HTTP, JPA/SQL, delegation, etc.)
- The persistence technology (database type, in-memory vs external)
- Cross-cutting concerns (e.g. global exception handling, validation)

Focus on:
- `src/main/java/**/*.java`
- `src/main/resources/`
- `pom.xml`

Exclude method names, DTO types, field names, and return types from the diagram.

### Step 2: Generate the Mermaid Diagram

Produce a `flowchart TD` diagram where:

- Each node shows the real class name
- The client node reflects the discovered client type (e.g. `Browser`, `Mobile App`, `Client Service`)
- Each Controller has its own edge from the client node, labeled with that controller's REST endpoints
- Other edges use short descriptive labels (e.g. `delegates`, `maps entity ↔ response`, `JPA / SQL`)
- Classes are grouped into subgraphs by layer (Controller Layer, Service Layer, Data Access Layer, etc.)
- Cross-cutting concerns connect to the layers they affect

### Step 3: Write to File

Write the diagram to `architecture-diagram.md` in the project root:

````markdown
# Architecture Diagram

```mermaid
flowchart TD
  ...
```
````
