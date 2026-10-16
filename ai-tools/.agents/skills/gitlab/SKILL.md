---
name: gitlab
description: Inspect this project's GitLab resources with the local GitLab CLI.
---

# GitLab

- Use the local `glab` CLI for all GitLab operations. Default to read-only commands such as `glab repo view`, `glab mr list/view/diff`, and `glab issue list/view`.
- Ask the user and wait for permission before any command that creates, edits, comments, closes, merges, or otherwise changes GitLab state. Never expose credentials.
- `glab` is not installed for this user; do not install it. If unavailable when this skill is used, explain that and stop rather than switching clients.
