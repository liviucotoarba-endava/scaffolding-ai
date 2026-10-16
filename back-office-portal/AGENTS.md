# Repository Guidance

## Project
This is a plain HTML, CSS, and JavaScript back office demo with a Node.js static server. It uses sample users in `index.html`; it does not call the users API or order notifier. There are no package dependencies, framework, or build step.

## Development
- Keep page structure and sample data in `index.html`, presentation in `styles.css`, browser behavior in `app.js`, and local static serving in `server.js`.
- Follow the existing DOM and CSS patterns; keep changes focused. Preserve case-insensitive name/email search, whitespace trimming, result counts, and the empty state unless the requested behavior changes them.
- Preserve accessible labels, table headers, status announcements, keyboard focus, and responsive table scrolling. Use `textContent` for dynamic text.
- The server binds to `127.0.0.1` on `PORT` or `3000` and serves an explicit file allowlist. Update that allowlist when adding an asset that must be served.
- Add or update tests for behavior changes. Run focused checks first, then broader checks as appropriate.
- Use `npm test` for the full suite and report checks and failures accurately. Use `npm start` for browser checks; no dependency installation is needed.
- Preserve unrelated user changes and review your diff before finishing.

## Testing
- Follow `tests/app.test.js`: use `node:test`, `node:assert/strict`, and `node:vm` to run `app.js` with fresh DOM doubles for each test. Dispatch input events and check visible names, result-count text, and empty-state visibility.
- Cover relevant name/email matching, case and whitespace handling, zero/one/multiple matches, and clearing a search. Role text is currently excluded from search.
- These are isolated behavior tests. DOM doubles do not verify actual HTML wiring, CSS layout, keyboard navigation, or browser rendering.
- There is no checked-in HTTP or browser integration suite. For integration work, follow the local `integration-testing` skill and accurately distinguish automated checks from browser checks.
- For UI changes, inspect the served page at wide and narrow widths, keyboard focus, the matching rows and count, and the empty-state/reset flow as relevant. Report browser checks that could not be run.
- Run focused tests with `node --test tests/app.test.js`, then `npm test` when broader behavior is affected. Check JavaScript syntax with `node --check app.js` and `node --check server.js`.

## Local AI Tools
- Read the relevant workflow in `.agents/skills`: `bug-fixing`, `unit-testing`, `integration-testing`, or `frontend-development` for development; `create-jira-ticket` for ticket drafts; `github`, `gitlab`, `jenkins`, or `sonar` for remote tooling.
- Use `.codex/agents/portal-ui-implementer.toml` when the user requests the portal implementation agent.
- `.codex/config.toml` carries the shared Atlassian MCP configuration. `.codex/hooks.json` checks Conventional Commit subjects for supported Codex-issued Git commands.
- The portal has no Java or MapStruct code. Use `frontend-development` for its HTML, CSS, DOM, and accessibility conventions.

## Git and Remote Access
- Never commit or push automatically. Get specific user approval before each commit or push.
- For GitHub, GitLab, Jenkins, and other remote systems, use the configured local CLI and default to read-only commands.
- Get approval before remote writes, including creating, editing, commenting on, or merging work, and before triggering or changing jobs.
- Never print, copy, or expose credentials, tokens, or secret values.

## Branches and Commits
- Keep changes small and integrate frequently with `main`; use short-lived `codex/` branches when branching, not long-lived branches.
- Use lowercase Conventional Commit subjects: `type(scope): description` (for example, `feat(portal): add role filter`). Add `!` before the colon for breaking changes.
