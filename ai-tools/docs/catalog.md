# AI tools catalog

Snapshot: 13 October 2026. All 27 source skills are represented by 12 collected skill bundles, alongside four
user-provided skills (16 total). Identical bundles share one copy; different workflows have distinct
folder and `name` values.

Source key: **portal** = `back-office-portal`; **orders** = `order-service-notifier`; **users** = `user-service-api`.

## Skills

| Toolkit skill                                                                       | Sources               | Purpose                                                       |
|-------------------------------------------------------------------------------------|-----------------------|---------------------------------------------------------------|
| [bug-fixing](../.agents/skills/bug-fixing/SKILL.md) | portal, orders, users | Shared debugging workflow with conditional UI checks. |
| [create-jira-ticket](../.agents/skills/create-jira-ticket/SKILL.md)                 | portal, orders, users | Draft tickets; includes three complete examples.              |
| [create-worktree](../.agents/skills/create-worktree/SKILL.md)                       | user-provided         | Prepare ticket-scoped multi-repo worktrees and planning files. |
| [frontend-development](../.agents/skills/frontend-development/SKILL.md)             | portal                | HTML, CSS, DOM, accessibility, and responsive UI.             |
| [github](../.agents/skills/github/SKILL.md)                                         | portal, orders, users | Use the local GitHub CLI.                                     |
| [gitlab](../.agents/skills/gitlab/SKILL.md)                                         | portal, orders, users | Use the local GitLab CLI when available.                      |
| [integration-testing-java](../.agents/skills/integration-testing-java/SKILL.md)     | orders, users         | Spring HTTP wiring and project-specific dependency isolation. |
| [integration-testing-portal](../.agents/skills/integration-testing-portal/SKILL.md) | portal                | Static HTTP serving and real browser checks.                  |
| [jenkins](../.agents/skills/jenkins/SKILL.md)                                       | portal, orders, users | Read Endava Jenkins using the local CLI and credential file.  |
| [mapstruct-mapping](../.agents/skills/mapstruct-mapping/SKILL.md)                   | orders, users         | Shared Java mapping conventions.                              |
| [mutation-coverage](../.agents/skills/mutation-coverage/SKILL.md)                   | user-provided         | Add missing unit tests to improve PIT mutation coverage.      |
| [owasp-vulnerabilities](../.agents/skills/owasp-vulnerabilities/SKILL.md)           | user-provided         | Fix dependency vulnerabilities or maintain OWASP suppressions. |
| [sonar](../.agents/skills/sonar/SKILL.md)                                           | portal, orders, users | Use the local Sonar CLI.                                      |
| [trivy-vulnerabilities](../.agents/skills/trivy-vulnerabilities/SKILL.md)           | user-provided         | Fix dependency vulnerabilities or maintain Trivy suppressions. |
| [unit-testing-java](../.agents/skills/unit-testing-java/SKILL.md)                   | orders, users         | JUnit, AssertJ, Mockito, and mapper tests.                    |
| [unit-testing-portal](../.agents/skills/unit-testing-portal/SKILL.md)               | portal                | Node tests and isolated DOM doubles.                          |

The three bug-fixing versions are merged into one shared workflow: the Java versions differed only in their project descriptions, and the portal browser checks now apply to UI bugs. Testing remains split into Java and portal skills because their tools and verification methods differ. Jira examples and supporting files stay with their skill folder.

## Agents and configuration

| Item                                                                     | Source / use                                                        |
|--------------------------------------------------------------------------|---------------------------------------------------------------------|
| [portal-ui-implementer](../.codex/agents/portal-ui-implementer.toml)     | Portal implementation and verification.                             |
| [kafka-event-implementer](../.codex/agents/kafka-event-implementer.toml) | Notifier implementation and verification.                           |
| [rest-api-implementer](../.codex/agents/rest-api-implementer.toml)       | Users API implementation and verification.                          |
| [dava-code-reviewer](../.codex/agents/dava-code-reviewer.toml)           | User-provided Dava workflow: technical and Jira requirements review; pending GitHub comments only. |
| [Trust Me Bro](../.codex/agents/trust-me-bro.toml)                       | User-provided multi-repo Jira implementation workflow with setup and review approvals, sequential repository agents, and optional tests. |
| [JIRA Developer](../.codex/agents/jira-developer.toml)                 | User-provided workflow for current-repo Jira scope, existing code conventions, and clean compilation; no added tests or code comments. |
| [Caveman](../.codex/agents/caveman.toml)                               | User-provided communication style for short, direct replies with minimal tokens. |
| [Atlassian MCP config](../.codex/config.toml)                            | Identical in all three sources; one copy.                           |
| [Commit hook config](../.codex/hooks.json)                               | Source paths normalized to `ai-tools/.codex/hooks/commit_guard.py`. |
| [Commit guard script](../.codex/hooks/commit_guard.py)                   | Identical in all three sources; one copy.                           |

The three stack-specific implementation agents have write access and require the matching application code and tests.
Trust Me Bro coordinates multi-repo work using `create-worktree` and each target repository's own agents and guidance.
Dava Code Reviewer uses a read-only local sandbox and the `github` skill (`gh` CLI); it requires an exact MR and Jira
description, and leaves review comments pending for manual publication. The commit guard handles supported simple Codex-issued `Bash` Git commands; it checks subjects and local
remote-tracking refs and does not grant commit/push permission.

## Toolkit guidance

- [Toolkit instructions](../AGENTS.md): guidance at the `ai-tools` level.
- [AGENTS.md generation prompt](generate-agents-md-prompt.md): reusable prompt for discovering and documenting a target
  repository's architecture, workflows, and conventions.

This is a snapshot. Editing it does not synchronize changes into the demos. The remote skills retain the original
Mac-specific setup examples; copying a skill does not install a CLI or authenticate a service.
