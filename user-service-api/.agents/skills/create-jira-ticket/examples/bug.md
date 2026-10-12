# Summary
Return a clear error when a requested user does not exist

# Issue Type
Bug

# Description

## Context
Requesting a user ID that is not in the database currently returns an unexpected server error.

## Steps to Reproduce
1. Start the API with an empty users table.
2. Send `GET /api/users/99`.

## Expected Behavior
The API returns `404 Not Found` with a useful error detail.

## Actual Behavior
The API returns `500 Internal Server Error`.

## Acceptance Criteria
- [ ] Missing users return `404 Not Found`.
- [ ] The response includes a clear error detail.
