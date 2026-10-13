# Use with the Codex app or CLI

Start here to browse the collection, explain a workflow, or draft a Jira ticket.
For implementation, use a separate target project with the relevant tools.

## Codex app

1. Add `ai-tools` as a local project folder.
2. Make it the primary folder if the project has multiple folders.
3. Start a new chat and ask Codex to read `README.md` and `docs/catalog.md`.
4. Select a skill available in that chat, or ask Codex to read its `SKILL.md`.

The primary folder controls automatic discovery of project instructions,
skills, and configuration. Secondary folders are available for file access,
but their project configuration is not automatically discovered.
[Official project guide](https://learn.chatgpt.com/docs/projects)

## Codex CLI

From the `scaffolding-ai` repository root:

```sh
codex --cd ai-tools
```

`--cd` sets the working directory. In the chat, use `/skills` or type `$` to
select a skill. Try: `$create-jira-ticket Draft a bug ticket for a user search
that ignores uppercase letters. Do not create it in Jira.`
[CLI options](https://learn.chatgpt.com/docs/cli/reference),
[Skill invocation](https://learn.chatgpt.com/docs/build-skills)

Skills here live in `.agents/skills`. Discovery scans the working directory
and its ancestors up to the repository root; it does not scan sibling folders.
Starting at the repository root or inside a sibling app will not load these
collection skills automatically. [Skill discovery](https://learn.chatgpt.com/docs/build-skills)

## Reuse in a scratch project

Copy only the needed skill folders to the target's `.agents/skills`, keeping
their references and scripts. Add a relevant `.codex/agents/*.toml` if needed.
Use `portal-ui-implementer` with portal skills; use `rest-api-implementer` or
`kafka-event-implementer` with the Java skills in their matching project.
Project agents are standalone TOML files. [Custom agents](https://learn.chatgpt.com/docs/agent-configuration/subagents)

Keep toolkit instructions in `ai-tools/AGENTS.md`. For a scratch application, use its own project guidance.
Reopen Codex in the target and confirm the selected skills.

## Optional integrations and hooks

Remote workflows need their listed CLIs, account access, and authentication.
The Atlassian config is optional; drafting a ticket needs no Jira connection.
Jenkins paths describe this Mac; the GitLab CLI example needs a local `glab`.
Keep credentials outside this collection. No installation or login is automatic.

The commit hook needs Python 3. Read `.codex/hooks.json` and its script, then
use `/hooks` in the CLI to review and trust it. Its current script path is
`$(git rev-parse --show-toplevel)/ai-tools/.codex/hooks/commit_guard.py`.
If moving into a standalone Git project, remove the `ai-tools/` path segment.
Hooks run from the session working directory and require explicit trust.
[Hook setup and trust](https://learn.chatgpt.com/docs/hooks)
