# Use with IntelliJ IDEA

Use IntelliJ to inspect the examples, select code, and demonstrate a focused
Codex workflow. The collection is presentation material, so implementation
belongs in a separate scratch project or an explicitly selected target.

## Open the collection

1. Open `ai-tools` as the project folder.
2. Read `README.md`, then open `docs/catalog.md` to choose an example.
3. Enable the Codex integration available in your IntelliJ installation.
4. Open **AI Chat** and select **Codex**, then sign in if requested.

These are the current official JetBrains entry points; availability and setup
can depend on the installed IDE and integration version.
[Official IDE guide](https://learn.chatgpt.com/docs/codex/ide)

Open a skill's `SKILL.md` and give Codex its path or the selected text. Example:

> Read `.agents/skills/create-jira-ticket/SKILL.md` and its bug example.
> Draft a ticket for an empty user search returning no results.
> Do not create the ticket in Jira or change files.

If your integration offers a skill selector, choose the skill there. Confirm
what Codex loaded before the presentation. Do not assume every IntelliJ
version exposes the CLI's hook controls or custom agent features.

## Demonstrate implementation

Create a separate scratch project, copy only the needed skill folders into
its `.agents/skills`, and open that project in IntelliJ. Keep references and
scripts with each skill. Add one relevant `.codex/agents/*.toml` if supported.
Choose portal skills for the UI, Java skills for either Spring service.

Toolkit instructions stay at `ai-tools/AGENTS.md`; use the scratch application's
own project guidance when demonstrating implementation.
Starting Codex from a sibling project does not discover `ai-tools` skills.
[Skill discovery](https://learn.chatgpt.com/docs/build-skills)

## Terminal fallback

From IntelliJ's terminal at the `scaffolding-ai` repository root:

```sh
codex --cd ai-tools
```

Use `/skills` to select a skill and `/hooks` to inspect optional hooks.
For a scratch project, replace `ai-tools` with that project's path.
See [Codex app and CLI](codex-app-cli.md) for reuse, hook paths, and prerequisites.
Remote tools still require local access and authentication; browsing and
drafting work without those connections. Keep demos free of commits and pushes.
