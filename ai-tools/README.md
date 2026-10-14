# AI Tools

Shared AI support materials collected from the three demos in `scaffolding-ai`: `back-office-portal`,
`order-service-notifier`, and `user-service-api`. Use this folder to present, compare, and selectively reuse their Codex
scaffolding.

## Contents

| Location                             | Contains                                                                  |
|--------------------------------------|---------------------------------------------------------------------------|
| [`AGENTS.md`](AGENTS.md)             | Instructions for maintaining and using this toolkit.                      |
| [`.agents/skills/`](.agents/skills/) | 16 skills, including Jira examples, worktree setup, mutation coverage, and vulnerability fixes. |
| [`.codex/agents/`](.codex/agents/)   | Seven custom agents for implementation, orchestration, review, and concise replies. |
| [`.codex/`](.codex/)                 | Shared Atlassian MCP configuration and Conventional Commit hook.          |

Identical tools are stored once. Bug fixing uses one shared workflow. Stack-specific testing skills use `-portal` or `-java` suffixes;
their references are updated within this collection. See the [catalog](docs/catalog.md) for every tool and its source.

## Try it

Open `ai-tools` as the primary folder of a local Codex project, or run this from the presentation repository root:

```sh
codex --cd ai-tools
```

Try: `Use $create-jira-ticket to draft a story for a user-directory role filter. Return the draft for review.`

For development tasks, choose the matching skill and agent in a target project with application code and tests. Follow
the short guides for [Codex app and CLI](docs/codex-app-cli.md) or [IntelliJ / JetBrains](docs/intellij.md).

To generate project instructions, use the [AGENTS.md generation prompt](docs/generate-agents-md-prompt.md) in the target
repository.

## Reuse and maintenance

This folder is a snapshot; the three source demos retain their own scaffolding. Select tools for a separate scratch
project and adapt its project instructions and hook path. The hook currently points to `ai-tools` within this Git
repository. Remote skills retain local CLI and authentication examples; they require your own setup.

Maintain the catalog when changing tools. This collection can later be extracted into a standalone
repository. It has no automatic installer or synchronization process.
