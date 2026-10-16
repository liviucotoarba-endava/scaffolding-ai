---
name: integration-testing-portal
description: Verify the portal's static HTTP serving and real page interactions, distinguishing browser checks from isolated Node tests.
---

# Integration Testing

- Read `AGENTS.md`, `server.js`, `index.html`, `app.js`, `styles.css`, and relevant tests first. The existing `tests/app.test.js` uses DOM doubles; there is no checked-in HTTP or browser integration suite.
- Start the portal with `npm start` and check `http://127.0.0.1:3000`, or set `PORT` to an available local port. The demo uses sample users and needs no users API, Kafka broker, or database.
- For HTTP coverage, check the requested static route, status, content type, and relevant response content. Cover the server's allowlist and unknown-route `404` behavior when affected. Use `node:test` and Node's HTTP client or `fetch` for automated checks; isolate ports and reliably stop any server started by a test. Wait for readiness instead of using fixed sleeps.
- For page interactions, use available browser tools against the served page. Exercise the actual search input, inspect visible rows, count text, and empty-state visibility, and check reset behavior. Cover case/whitespace handling and zero/one/multiple matches when relevant.
- Check keyboard access, visible focus, accessible labels/status, narrow-screen table scrolling, and browser errors when the change affects them. Node DOM doubles cannot establish layout or accessibility behavior in a browser.
- Keep automated checks dependency-free where practical. If an installed browser test runner already exists, follow its conventions; introduce a runner only when it is needed for the requested task.
- Run any added or updated Node integration test file with `node --test`, then `npm test` for the full suite. Report HTTP checks, browser checks, and automated tests separately; state any verification that could not be performed.
