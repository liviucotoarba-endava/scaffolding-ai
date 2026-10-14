---
name: owasp-vulnerabilities
description: Skill for resolving existing OWASP vulnerabilities
---

# OWASP Vulnerabilities

Skill for resolving existing OWASP vulnerabilities. Use this when asked to fix or suppress some vulnerabilities.

Based on user input, you need to either:

1. Fix an existing OWASP vulnerability by updating the current version of a dependency to a newer minor version
2. Suppress an existing OWASP vulnerability by adding/editing a suppression rule in the `owasp-suppression.xml` file

## Context

User might provide an input detailing the current scope / context for this task.

If needed, run command the following command to get the current Owasp status:

`mvn -s settings.xml org.owasp:dependency-check-maven:aggregate -Dformats=JSON`

## Guidelines

- Add the requested / required changes, wither in `owasp-suppression.xml` or `pom.xml` or `package.json` files,
  depending on the case.
- When suppressing a vulnerability, take into account the current system date so that the `suppress until` overlaps it
  with 1-2 months in the future. Also, if you see expired suppressions, update to 1-2 months in the future.
- If needed, check the results by running the following command:
  `mvn -s settings.xml org.owasp:dependency-check-maven:aggregate -Dformats=JSON`
