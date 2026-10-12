---
name: github
description: Inspect and manage this project's GitHub repository using the local GitHub CLI.
---

# GitHub

- Use the locally installed `gh` CLI for all GitHub operations and its existing authentication. Don’t use a browser or another client for GitHub work, and never expose credentials.
- Default to read-only commands such as `gh repo view`, `gh pr view/list/diff/checks`, and `gh issue view/list`.
- Ask the user and wait for permission before any command that creates, edits, comments, reviews, closes, merges, or otherwise changes GitHub state. Treat unclear effects as writes.
