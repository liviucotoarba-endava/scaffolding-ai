---
name: frontend-development
description: Implement portal UI changes using its plain HTML, CSS, and JavaScript conventions, accessible controls, and responsive layout.
---

# Frontend Development

- Read `AGENTS.md` and the relevant HTML, styles, browser script, and tests before changing the UI. Keep this dependency-free demo simple; use its existing DOM and CSS patterns.
- Keep semantic structure and sample users in `index.html`, styling in `styles.css`, and interactions in `app.js`. Keep element IDs, selectors, and event wiring consistent across those files.
- Use native controls with associated labels, table header scopes, visible keyboard focus, and useful status/empty-state text. Preserve the `role="status"` result count and `hidden` visibility behavior.
- Preserve name/email matching, case-insensitive comparison, whitespace trimming, singular/plural counts, and reset behavior unless the requested feature changes them. Keep row visibility and displayed counts consistent when adding another filter.
- Use `textContent` for dynamic text. Follow the existing spacing, colors, `600px` responsive breakpoint, and table-scroll container; check both wide and narrow layouts when changing presentation.
- Add new served assets to the explicit allowlist in `server.js`. Keep sample-data behavior unless API integration is part of the requested task.
- Use `unit-testing-portal` for automated behavior coverage and `integration-testing-portal` for actual page wiring and browser checks. Run `npm test`, check affected JavaScript syntax with `node --check`, and report browser verification accurately.
