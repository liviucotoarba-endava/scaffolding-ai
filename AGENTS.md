# Presentation Repository Guidance

This repository supports a presentation on scaffolding AI onto projects. It contains three independent demo applications
and the shared `ai-tools` collection.

- Read the target application's `AGENTS.md` before application work; run its checks from its own folder.
- `ai-tools` collects skills, implementation agents, hooks, MCP configuration, project instructions, and prompts. See
  its README and catalog for the sources and variant names.
- Toolkit instructions live at `ai-tools/AGENTS.md`.
- Toolkit maintenance belongs in `ai-tools`; keep the existing demo scaffolds unchanged unless the user explicitly
  requests changes there.
- Keep presentation documentation concise. Distinguish prepared materials from demonstrations and checks still to be
  completed.
- Verify the changed files and preserve unrelated work. Shared tools are copied snapshots; changes do not propagate
  automatically.
- Never expose credentials. Default to read-only remote access; remote writes require explicit authorization.
- Do not commit or push unless the user explicitly requests it.
