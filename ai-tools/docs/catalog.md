# AI tools catalog

Snapshot: 13 October 2026. All 27 source skills are represented by 12 skill bundles. Identical bundles share
one copy; different workflows have distinct folder and `name` values.

Source key: **portal** = `back-office-portal`; **orders** = `order-service-notifier`; **users** = `user-service-api`.

## Skills

| Toolkit skill                                                                       | Sources               | Purpose                                                       |
|-------------------------------------------------------------------------------------|-----------------------|---------------------------------------------------------------|
| [bug-fixing](../.agents/skills/bug-fixing/SKILL.md) | portal, orders, users | Shared debugging workflow with conditional UI checks. |
| [create-jira-ticket](../.agents/skills/create-jira-ticket/SKILL.md)                 | portal, orders, users | Draft tickets; includes three complete examples.              |
| [frontend-development](../.agents/skills/frontend-development/SKILL.md)             | portal                | HTML, CSS, DOM, accessibility, and responsive UI.             |
| [github](../.agents/skills/github/SKILL.md)                                         | portal, orders, users | Use the local GitHub CLI.                                     |
| [gitlab](../.agents/skills/gitlab/SKILL.md)                                         | portal, orders, users | Use the local GitLab CLI when available.                      |
| [integration-testing-java](../.agents/skills/integration-testing-java/SKILL.md)     | orders, users         | Spring HTTP wiring and project-specific dependency isolation. |
| [integration-testing-portal](../.agents/skills/integration-testing-portal/SKILL.md) | portal                | Static HTTP serving and real browser checks.                  |
| [jenkins](../.agents/skills/jenkins/SKILL.md)                                       | portal, orders, users | Read Endava Jenkins using the local CLI and credential file.  |
| [mapstruct-mapping](../.agents/skills/mapstruct-mapping/SKILL.md)                   | orders, users         | Shared Java mapping conventions.                              |
| [sonar](../.agents/skills/sonar/SKILL.md)                                           | portal, orders, users | Use the local Sonar CLI.                                      |
| [unit-testing-java](../.agents/skills/unit-testing-java/SKILL.md)                   | orders, users         | JUnit, AssertJ, Mockito, and mapper tests.                    |
| [unit-testing-portal](../.agents/skills/unit-testing-portal/SKILL.md)               | portal                | Node tests and isolated DOM doubles.                          |

The three bug-fixing versions are merged into one shared workflow: the Java versions differed only in their project descriptions, and the portal browser checks now apply to UI bugs. Testing remains split into Java and portal skills because their tools and verification methods differ. Jira examples and supporting files stay with their skill folder.

## Agents and configuration

| Item                                                                     | Source / use                                                        |
|--------------------------------------------------------------------------|---------------------------------------------------------------------|
| [portal-ui-implementer](../.codex/agents/portal-ui-implementer.toml)     | Portal implementation and verification.                             |
| [kafka-event-implementer](../.codex/agents/kafka-event-implementer.toml) | Notifier implementation and verification.                           |
| [rest-api-implementer](../.codex/agents/rest-api-implementer.toml)       | Users API implementation and verification.                          |
| [Atlassian MCP config](../.codex/config.toml)                            | Identical in all three sources; one copy.                           |
| [Commit hook config](../.codex/hooks.json)                               | Source paths normalized to `ai-tools/.codex/hooks/commit_guard.py`. |
| [Commit guard script](../.codex/hooks/commit_guard.py)                   | Identical in all three sources; one copy.                           |

These are implementation agents with write access. Their workflows still require the matching application code and
tests. The commit guard handles supported simple Codex-issued `Bash` Git commands; it checks subjects and local
remote-tracking refs and does not grant commit/push permission.

## Toolkit guidance

- [Toolkit instructions](../AGENTS.md): guidance at the `ai-tools` level.

This is a snapshot. Editing it does not synchronize changes into the demos. The remote skills retain the original
Mac-specific setup examples; copying a skill does not install a CLI or authenticate a service.
