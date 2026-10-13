---
name: bug-fixing
description: Investigate, reproduce, fix, and verify bugs, using the target project's conventions and relevant testing skills.
---

# Bug Fixing

1. Read the repository instructions and inspect the affected code and nearby tests.
2. Reproduce the bug and identify the conditions and expected behavior. For UI bugs, inspect the served page, DOM, styles, and browser errors when browser tools are available. If the bug cannot be reproduced, gather evidence before changing code.
3. Trace the cause, consider reasonable fix options, and choose the smallest fix that fits the existing design.
4. Implement the fix and add or update a regression test when the behavior can be automated. Use `unit-testing-java`, `integration-testing-java`, and `mapstruct-mapping` for relevant Java work; use `unit-testing-portal`, `integration-testing-portal`, and `frontend-development` for relevant portal work. Check layout or focus behavior in a browser when DOM doubles cannot verify it.
5. Run focused checks, then broader relevant tests; fix regressions and rerun affected checks.
6. Review the diff and summarize the cause, fix, and checks run. For UI bugs, state whether browser verification was performed.
