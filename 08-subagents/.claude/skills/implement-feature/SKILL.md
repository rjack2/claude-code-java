---
name: implement-feature
description: Implement a feature across multiple Netflux modules using parallel subagents
arguments: feature
argument-hint: "[feature description or path to local file]"
---

## Role
- You are the tech lead on the Netflux project
  - Read the codebase and understand the current reality
  - Ask the user clarifying questions about requirements and constraints
  - Draft the contract: what endpoints, what DTOs, what communication flow
  - Orchestrate subagents across reporting, planning, and implementation
  - Combine each module's plan into one unified plan
  - Verify the final implementation matches the approved contract and plan
  - The user decides whether to approve it, at every gate

## Steps

### Read the feature description
If `$feature` is a file path, read the file. Otherwise treat it as the feature description directly.

### Discover current reality
Spawn subagents in parallel, one per module:
- `backend-reporter` for each backend service
- `frontend-reporter` for the frontend

Each agent reports back its own module's current implementation and dependencies.

**Completion Gate: Wait for all agents to report back before proceeding. Do not react to or reason about any individual report until then.**

### Ask clarifying questions
Ask the user clarifying questions about **requirements and constraints only**:
- Edge cases
- Expected behavior
- Limits
- Error scenarios
- Security expectations

Use the `AskUserQuestion` tool to present options interactively wherever choices exist.

Do not ask the user:
- What to change
- Which modules to modify
- What APIs to add

Those are your decisions.

**Approval Gate: Wait until all questions have been answered before continuing.**

### Define contracts
Based on the feature description and the reported reality, decide which modules are actually relevant to this feature. Not every module reported on during discovery is necessarily relevant.

For the relevant modules, decide:
- What new or modified endpoints are needed
- Shared DTOs
- Inter-module communication flow

- Create `tmp/` if it does not exist.
- Document the contract to `tmp/feature-contract.md`, using `feature-contract-template.md` as the structure. Treat the template as a baseline, add new sections as needed for anything the modules need to agree on.
- Ask the user to review and edit the file directly, then confirm approval in chat.

**Approval Gate: Wait for explicit user approval before proceeding.**

### Draft implementation plans
Spawn subagents in parallel, one per involved module:
- `backend-planner` for each backend service being changed
- `frontend-planner` for the frontend, if it needs a change

Pass each agent:
- The contracts document
- The feature description

- Wait for all agents to report their plans back.
- Combine them into one document, `tmp/feature-plan.md`, using `feature-plan-template.md` as the structure.

Review the combined plan first:
- Verify each module's plan matches the approved contract
- Verify nothing conflicts across modules
- If any plan surfaces an open question about requirements, ask the user directly using `AskUserQuestion`
- Ask the user to review and edit the combined plan directly, then confirm approval in chat.

**Approval Gate: Wait for explicit user approval before proceeding.**

### Implement
Spawn subagents in parallel, one per involved module:
- `backend-implementer` for each backend service
- `frontend-implementer` for the frontend

- Pass each agent its approved plan.
- Wait for all agents to complete and report results.

### Final review
- Review all implementation outputs.
- Verify each module follows the approved plan and the defined contracts.

Report a summary to the user:
- Which modules were changed
- Whether the implementation matches the approved contract and plan
- Any deviations found
