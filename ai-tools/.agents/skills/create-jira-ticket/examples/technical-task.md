# Summary
Add automated coverage for input validation

# Issue Type
Task

# Description

## Context
Input validation rules need focused automated coverage so invalid requests are rejected consistently.

## Scope
Cover the existing required fields, supported values, and boundary limits using the project's current test conventions.

## Acceptance Criteria
- [ ] Tests cover valid input, missing required fields, unsupported values, and relevant boundaries.
- [ ] Invalid input is rejected before processing causes side effects.
- [ ] Tests pass with the project's documented verification command.
