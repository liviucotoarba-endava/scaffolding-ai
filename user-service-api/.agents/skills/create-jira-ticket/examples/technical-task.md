# Summary
Add validation for user request fields

# Issue Type
Task

# Description

## Context
User request fields should be checked before service logic runs.

## Scope
Require a non-blank name and a valid email address, with limits matching the entity fields.

## Acceptance Criteria
- [ ] Invalid requests return `400 Bad Request`.
- [ ] Valid requests continue through the existing API flow.
- [ ] Tests cover the validation rules.
