# Scaffolding AI onto Projects

A practical, demo-led presentation for the AI development community about the gap between having an AI assistant
available and using it effectively in a project.

The session shows how to configure AI support for existing repositories, prepare new projects with that support from the
start, and reuse capabilities across a team. It draws on commercial project experience and connects each configuration
addition to a coding task and an observable result.

This repository contains the presentation outline, preparation checklist, three demo sub-applications, and the shared [
`ai-tools`](ai-tools/README.md) collection. The collection brings their AI support materials together for presentation
and selective reuse; the original demo scaffolds remain independent.

## Repository layout

All sub-applications belong to this single GitHub repository and are versioned together from the repository root.

| Folder                                                        | Demo                                                                                       | Setup and verification                                                                         |
|---------------------------------------------------------------|--------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| [`user-service-api/`](user-service-api/README.md)             | Java 25 / Spring Boot users REST API with an in-memory H2 database.                        | Maven wrapper; see the application README.                                                     |
| [`order-service-notifier/`](order-service-notifier/README.md) | Java 25 / Spring Boot Kafka producer with manual HTTP triggers.                            | Maven wrapper; tests run without a broker.                                                     |
| [`back-office-portal/`](back-office-portal/README.md)         | HTML, CSS, and JavaScript frontend using sample users.                                     | Node.js; no dependency installation or build step.                                             |
| [`ai-tools/`](ai-tools/README.md)                             | Shared skills, implementation agents, hooks, MCP configuration, instructions, and prompts. | See the [catalog](ai-tools/docs/catalog.md) and [usage guide](ai-tools/docs/codex-app-cli.md). |

Run each application's commands from its own folder. The demos currently run independently; the frontend uses mock data
and the notifier does not call the user API.

The root `.gitignore` excludes IDE metadata, operating-system files, logs, and local environment overrides throughout
the repository. Each application's `.gitignore` excludes its generated output. Source files, tests, application
configuration, Maven wrapper scripts and properties, and the frontend package lockfile are kept in version control.

## Codex scaffolding

Each application keeps a complete local scaffold:

- `AGENTS.md`: project context, conventions, skill routing, and verification commands.
- `.agents/skills/<name>/SKILL.md`: focused workflows and supporting examples where needed.
- `.codex/agents/<name>.toml`: an implementation agent with application-specific verification steps.
- `.codex/config.toml` and `.codex/hooks.json`: Atlassian MCP setup and a Conventional Commit guard.
- The portal and notifier READMEs include Codex launch instructions and presentation prompts.

Open the application folder as the Codex project, or use `codex --cd <folder>` from this root. When working from the
root, explicitly read the target application's `AGENTS.md` and relevant skill; supply its agent definition for a
requested implementation task.

Similar capabilities intentionally remain copied into every application. `ai-tools` stores identical tools once and
uses one shared bug-fixing workflow and separate Java and portal testing skills. Its 16 skills cover all 27 source
skills.
Use its [Codex app / CLI](ai-tools/docs/codex-app-cli.md) and [IntelliJ](ai-tools/docs/intellij.md) guides for selective
reuse; collection updates do not synchronize into the demos.

Toolkit instructions live in [`ai-tools/AGENTS.md`](ai-tools/AGENTS.md).

Use the local skill names shown in each application README. Follow
the [skills](https://learn.chatgpt.com/docs/build-skills)
and [custom agent](https://learn.chatgpt.com/docs/agent-configuration/subagents) formats when extending the scaffold.

## What the session covers

- Give an assistant the project context and instructions it needs to work effectively.
- Build an AI harness: context, instructions, tools, execution controls, and verification.
- Add relevant skills, an implementation agent, and reusable task prompts.
- Decide which capabilities belong in a repository and which belong in a shared toolkit.
- Select, install, version, and maintain shared capabilities across repositories.
- Verify AI-generated changes before accepting them.

## The AI harness

The harness is the environment around the assistant that guides how it works and how its output is checked.

| Building block     | Purpose                                                              |
|--------------------|----------------------------------------------------------------------|
| Context            | Explain the product, architecture, codebase, and current task.       |
| Instructions       | Describe project conventions, constraints, and expected workflows.   |
| Tools              | Provide the capabilities and integrations needed to complete a task. |
| Execution controls | Define where the assistant can act and when approval is required.    |
| Verification       | Use tests, checks, and review to assess the resulting changes.       |
