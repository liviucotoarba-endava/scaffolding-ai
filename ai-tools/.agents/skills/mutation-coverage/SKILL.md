---
name: mutation-coverage
description: Skill for increasing the mutation coverage of the project by adding missing tests.
---

# Mutation Coverage

Skill for increasing the mutation coverage of the project by adding missing unit tests

## Context

User will provide an input detailing the current scope / context for this task.

## Guidelines

- Add missing unit tests so that mutation coverage check won't fail, go as much as possible towards ~100%.
- You can run this command:
  `mvn -s settings.xml test-compile org.pitest:pitest-maven:mutationCoverage -DmutationThreshold=80 -DcoverageThreshold=80`
- Check result from the above terminal output. When that is fine, you can double-check at the end the auto generated
  target report files under: `target/pit-reports/`
- Keep existing conventions/patterns/principles/format of the tests.
- Change only the tests, not the main code.
