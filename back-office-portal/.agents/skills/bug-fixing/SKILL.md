---
name: bug-fixing
description: Investigate, reproduce, fix, and verify bugs in back-office-portal.
---

# Bug Fixing

1. Read the repository instructions and inspect the affected code and nearby tests.
2. Reproduce the bug and identify the conditions and expected behavior. For a UI bug, inspect the served page and its DOM, styles, and browser errors when browser tools are available. If it cannot be reproduced, gather evidence before changing code.
3. Trace the cause, consider reasonable fix options, and choose the smallest one that fits the existing design.
4. Implement the fix and add or update a regression test when the behavior can be automated. Follow the `unit-testing`, `integration-testing`, or `frontend-development` skill when relevant; use a browser check for layout or focus behavior that DOM doubles cannot verify.
5. Run focused checks, then broader relevant tests; fix regressions and rerun affected checks.
6. Review the diff and summarize the cause, fix, and checks run, including whether browser verification was performed.
