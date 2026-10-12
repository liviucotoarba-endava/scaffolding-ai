# Scaffolding AI onto Projects

A practical, demo-led presentation for the AI development community about the gap between having an AI assistant available and using it effectively in a project.

The session shows how to configure AI support for existing repositories, prepare new projects with that support from the start, and reuse capabilities across a team. It draws on commercial project experience and connects each configuration addition to a coding task and an observable result.

**Duration:** 60 minutes, including questions.

**Demo tool:** Codex, with general principles distinguished from tool-specific configuration so they can be applied to other assistants.

This repository contains the presentation outline, preparation checklist, and three demo sub-applications. Each application has its own Codex instructions, skills, review agent, and reusable prompts. The shared toolkit described below is planned work.

## Repository layout

All sub-applications belong to this single GitHub repository and are versioned together from the repository root.

| Folder | Demo | Setup and verification |
| --- | --- | --- |
| [`user-service-api/`](user-service-api/README.md) | Java 25 / Spring Boot users REST API with an in-memory H2 database. | Maven wrapper; see the application README. |
| [`order-service-notifier/`](order-service-notifier/README.md) | Java 25 / Spring Boot Kafka producer with manual HTTP triggers. | Maven wrapper; tests run without a broker. |
| [`back-office-portal/`](back-office-portal/README.md) | HTML, CSS, and JavaScript frontend using sample users. | Node.js; no dependency installation or build step. |

Run each application's commands from its own folder. The demos currently run independently; the frontend uses mock data and the notifier does not call the user API.

The root `.gitignore` excludes IDE metadata, operating-system files, logs, and local environment overrides throughout the repository. Each application's `.gitignore` excludes its generated output. Source files, tests, application configuration, Maven wrapper scripts and properties, and the frontend package lockfile are kept in version control.

## Codex scaffolding

Each application keeps a complete local scaffold:

- `AGENTS.md`: project context, conventions, skill routing, and verification commands.
- `.agents/skills/<name>/SKILL.md`: focused workflows with local `agents/openai.yaml` metadata and default prompts.
- `.codex/agents/<name>.toml`: a review agent that does not edit files.
- `README.md`, under "Codex": launch instructions and reusable task examples.

Open the application folder as the Codex project, or use `codex --cd <folder>` from this root. When working from the root, explicitly read the target application's `AGENTS.md` and relevant skill; supply its agent definition for a requested review.

Similar capabilities intentionally remain copied into every application. Keep their workflow and review instructions aligned; vary the stack, paths, commands, and domain rules where needed. The Java testing skills share the same unit and integration conventions; the portal uses the same workflow with Node tests and browser checks. Keep each scaffold usable without files from a sibling application.

Use the local skill names shown in each application README. Follow the [skills](https://learn.chatgpt.com/docs/build-skills) and [custom agent](https://learn.chatgpt.com/docs/agent-configuration/subagents) formats when extending the scaffold.

## What the session covers

- Give an assistant the project context and instructions it needs to work effectively.
- Build an AI harness: context, instructions, tools, execution controls, and verification.
- Add relevant skills, a review agent, and reusable task prompts.
- Decide which capabilities belong in a repository and which belong in a shared toolkit.
- Select, install, version, and maintain shared capabilities across repositories.
- Verify AI-generated changes before accepting them.

## The AI harness

The harness is the environment around the assistant that guides how it works and how its output is checked.

| Building block | Purpose |
| --- | --- |
| Context | Explain the product, architecture, codebase, and current task. |
| Instructions | Describe project conventions, constraints, and expected workflows. |
| Tools | Provide the capabilities and integrations needed to complete a task. |
| Execution controls | Define where the assistant can act and when approval is required. |
| Verification | Use tests, checks, and review to assess the resulting changes. |

## Demo progression

The three sub-applications provide a REST API, a Kafka producer, and a frontend. Their local AI scaffolds are prepared; baseline comparisons and rehearsed development tasks remain to be recorded.

1. **Establish a baseline.** Run a task in the REST API application before adding scaffolding and save the result for comparison.
2. **Configure one application.** Add `AGENTS.md` and a relevant skill, then demonstrate implementation and verification. Introduce a review agent and reusable task prompts where they help the workflow.
3. **Reuse across applications.** Carry shared guidance into the Kafka producer and frontend, adding capabilities specific to each application. Use this stage to expose duplication and maintenance costs.
4. **Extract a shared toolkit.** Create a common AI-tools repository with selectable capabilities. Demonstrate selection, installation, version tracking, and one shared update reaching a consuming repository.
5. **Add an MCP integration.** Use one integration to support a concrete development task.
6. **Apply the workflow to new projects.** Show how teams can start with the scaffolding prepared and maintain it over time.

Each step should show the task, the configuration change, and the resulting behavior or verification evidence.

## Session agenda

| Time | Topic |
| --- | --- |
| 0–6 min | Commercial project story, observed problems, and intended improvements. |
| 6–11 min | Building blocks and how they form the AI harness. |
| 11–26 min | REST API: baseline → `AGENTS.md` → skill → implementation → verification. |
| 26–35 min | Kafka producer and frontend: reuse shared guidance and add application-specific capabilities. |
| 35–45 min | Shared toolkit: capability selection, installation, and updates. |
| 45–50 min | MCP integration supporting a concrete task. |
| 50–53 min | Scaffolding new projects and the team workflow. |
| 53–60 min | Questions and discussion. |

## Preparation checklist

Prepare in this order:

- [ ] Choose the example product and development tasks.
- [x] Create the REST API, Kafka producer, and frontend sub-applications in this repository.
- [ ] Establish and record baseline AI behavior before adding scaffolding.
- [x] Prepare repository instructions, relevant skills, a review agent, and reusable task prompts.
- [ ] Create a common AI-tools repository with selectable capabilities.
- [ ] Prepare one useful MCP integration.
- [ ] Demonstrate one shared capability update reaching a consuming repository.
- [ ] Rehearse the demo and save checkpoints and example results.
- [ ] Build the slides around the rehearsed sequence.

## Decisions to explain

- **Local versus shared:** Keep product context and repository-specific guidance local; identify reusable guidance and capabilities for the common toolkit.
- **Distribution:** Compare copy-paste, selective synchronization, Git submodules, and supported packages or plugins, including their maintenance tradeoffs.
- **Selection and versions:** Explain how each project chooses capabilities, tracks versions, and adopts updates.
- **Ownership:** Identify who maintains shared guidance and reviews changes before consumers adopt them.
- **Verification:** Show the checks and human review developers use before accepting AI output.
