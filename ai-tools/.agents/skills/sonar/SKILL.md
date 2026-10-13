---
name: sonar
description: Inspect Sonar projects and analysis results with the local Sonar CLI.
---

# Sonar

- Use the locally installed Sonar CLI for all Sonar operations. Default to read-only commands for project details, issues, and quality-gate results.
- Ask the user and wait for permission before submitting an analysis or running any command that changes Sonar state. Never expose credentials or tokens.
