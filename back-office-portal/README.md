# Back Office Portal

Minimal user-directory frontend with sample users, name/email search, result counts, and an empty state.

## Stack

- Plain HTML, CSS, and JavaScript
- Node.js static server and built-in test runner
- No package dependencies or build step

## Run

Run from this folder:

```sh
npm start
```

Open [http://127.0.0.1:3000](http://127.0.0.1:3000). Set `PORT` to use another local port, for example `PORT=3001 npm start`. No dependency installation is needed.

The page uses three sample users in `index.html`. Search matches names and email addresses, ignores case and surrounding whitespace, and updates the visible rows, result count, and empty state. The portal currently runs independently of User Service API and Order Service Notifier.

## Test

```sh
npm test
```

The eight existing tests run `app.js` with isolated DOM doubles using Node's built-in test runner. They cover name/email matching, case and whitespace handling, multiple matches, no matches, role exclusion, and clearing the search. They do not verify actual HTML wiring, CSS layout, or browser rendering. There is no checked-in HTTP or browser integration suite.

For a focused run:

```sh
node --test tests/app.test.js
```

## Codex

Open this folder as the Codex project, or launch from the repository root:

```sh
codex --cd back-office-portal
```

When working from the repository root, explicitly read this folder's `AGENTS.md` and relevant skill. The local scaffold mirrors the two Java services' tool categories with frontend-specific guidance:

| Tool | Location / purpose |
| --- | --- |
| Repository instructions | [AGENTS.md](AGENTS.md): page structure, search behavior, checks, and Git conventions |
| Bug fixing | [.agents/skills/bug-fixing](.agents/skills/bug-fixing/SKILL.md): reproduce and verify a focused portal fix |
| Unit testing | [.agents/skills/unit-testing](.agents/skills/unit-testing/SKILL.md): Node tests and isolated DOM doubles |
| Integration testing | [.agents/skills/integration-testing](.agents/skills/integration-testing/SKILL.md): static HTTP serving and real browser interactions |
| Frontend development | [.agents/skills/frontend-development](.agents/skills/frontend-development/SKILL.md): HTML, CSS, DOM, accessibility, and responsive conventions |
| Jira tickets | [.agents/skills/create-jira-ticket](.agents/skills/create-jira-ticket/SKILL.md): shared drafting workflow and reusable examples |
| Remote tools | Skills for [GitHub](.agents/skills/github/SKILL.md), [GitLab](.agents/skills/gitlab/SKILL.md), [Jenkins](.agents/skills/jenkins/SKILL.md), and [Sonar](.agents/skills/sonar/SKILL.md) |
| Implementation agent | [.codex/agents/portal-ui-implementer.toml](.codex/agents/portal-ui-implementer.toml): implement and verify portal changes |
| Atlassian MCP | [.codex/config.toml](.codex/config.toml): same server configuration as the Java services; authenticate before use |
| Commit hook | [.codex/hooks.json](.codex/hooks.json) and [.codex/hooks](.codex/hooks): check Conventional Commit subjects for simple `Bash` Git commit/push commands |

The GitHub, GitLab, Jenkins, and Sonar skills, complete Jira skill folder including examples, Atlassian MCP configuration, and commit-guard script are copied unchanged from User Service API. The hook configuration points to this portal's folder within the shared Git repository. Bug fixing and testing are adapted for JavaScript; `frontend-development` takes the place of the Java-only MapStruct skill. The portal implementation agent carries the local UI workflow.

The hook compares pushed subjects with local remote-tracking refs and does not grant commit or push approval. Inspect and trust the new hook in Codex before relying on it, as described in the [official hook documentation](https://learn.chatgpt.com/docs/hooks). The agent follows the [custom agent format](https://learn.chatgpt.com/docs/agent-configuration/subagents); skills follow the [local skill format](https://learn.chatgpt.com/docs/build-skills).

The CLI skills rely on existing local installations and authentication. The copied GitLab skill records that `glab` is unavailable on this Mac. Copying these files does not install tools or authenticate remote services.

### Presentation prompts

- `Use $unit-testing to cover a search that matches a user only through their email address. Keep tests dependency-free.`
- `Use $integration-testing to verify the served page's search, result count, empty state, and reset behavior. Report browser checks separately from Node tests.`
- `Use the portal-ui-implementer agent to add a role dropdown filter that works together with name/email search. Preserve accessible labels, counts, empty state, and responsive layout, and add focused tests.`
- `Use $frontend-development to improve the narrow-screen table layout and verify keyboard focus.`
- `Use $create-jira-ticket to draft a story for a role filter in the user directory. Return the draft for review.`

### Check the scaffold

```sh
npm test
node --check app.js
node --check server.js
git diff --check
```

After editing the scaffold, also check skill frontmatter, TOML/JSON syntax, README links, and the hook's path and allow/deny behavior. Changes to the AI support files alone do not require a new browser test suite.
