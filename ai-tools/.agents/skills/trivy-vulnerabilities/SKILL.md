---
name: trivy-vulnerabilities
description: Skill for resolving existing Trivy vulnerabilities
---

# Trivy Vulnerabilities

Skill for resolving existing Trivy vulnerabilities. Use this when asked to fix or suppress some vulnerabilities.

Based on user input, you need to either:

1. Fix an existing Trivy vulnerability by updating the current version of a dependency to a newer minor version
2. Suppress an existing Trivy vulnerability by adding/editing a suppression rule in the `.trivyignore.yaml` file

## Context

User should provide an input detailing the current scope / context for this task.

## Guidelines

- No need to re-run the Trivy scan after the fix/suppression
