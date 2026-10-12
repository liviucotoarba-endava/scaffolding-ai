# Summary
Reject invalid input with a clear error

# Issue Type
Bug

# Description

## Context
Illustrative example; confirm the behavior before filing. A request with a required field missing produces an unexpected error instead of a useful validation message.

## Steps to Reproduce
1. Prepare an otherwise valid request.
2. Remove a required field and submit the request.
3. Inspect the returned error and any resulting side effects.

## Expected Behavior
The request is rejected with a useful validation message and no processing side effects.

## Actual Behavior
The request returns an unexpected error without identifying the missing field.

## Acceptance Criteria
- [ ] A request missing the required field is rejected with a clear validation message.
- [ ] The rejected request causes no processing side effects.
- [ ] A regression test covers the reported input and expected behavior.
