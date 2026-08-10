# Contract: {feature name}

## Summary
{one paragraph describing the whole feature end to end}

## Relevant Modules
- {module name}: {why it is relevant}

## Requirements
- {every clarified behavior and edge case: limits, exclusions, tie-breaks, error handling, empty-state behavior, failure isolation}

## {module name}

### Endpoints
For each endpoint, provide a code block or table, whichever fits the DTO shape best:
```{language}
{endpoint signature and response/request DTO, e.g. an actual record definition}
```
- {list behavior notes: ordering, exclusions, size limits}

### Errors
- {status code}: {when it happens}

## Inter-Module Communication
{calls between modules, and why each is needed}

## Out of Scope
- {what is explicitly not changing}
