---
name: unit-testing
description: Add or update JavaScript unit tests using the portal's Node test runner and isolated DOM doubles.
---

# Unit Testing

- Read the repository instructions, affected code, and `tests/app.test.js` first. Keep each test focused on observable behavior, including relevant boundary and failure cases.
- Use `node:test` and `node:assert/strict`; the project needs no test framework or dependency installation.
- Follow the existing `node:vm` setup for browser-script tests: execute the real `app.js` in a fresh context with the DOM elements and event targets it needs. Extend doubles only for APIs the behavior actually uses.
- Dispatch the relevant event and assert visible rows, result-count text, and empty-state visibility. Keep inputs deterministic and reset state between tests; cover transitions such as clearing a search as well as isolated inputs.
- DOM doubles check JavaScript behavior. Use the `integration-testing` skill for real HTML wiring, HTTP serving, or browser behavior; do not describe a VM test as browser coverage.
- Run focused tests with `node --test tests/app.test.js`, or the actual affected test file. Use `node --test --test-name-pattern='clearing a search' tests/app.test.js` to focus on the existing reset case; run the full suite with `npm test` when the change affects broader behavior.
