# Back Office Portal

Small front-end mock for AI presentations, built with HTML, CSS, and vanilla JavaScript.
One page shows sample users with live search, a result count, and an empty state.

From this folder, run `npm start` with Node.js installed, then open
`http://localhost:3000`. Stop the server with Ctrl+C. To use a different port,
run `PORT=3001 npm start` on macOS/Linux.

You can also open `index.html` directly in a browser. No build step or external
dependencies are required.

- `index.html`: page and sample users
- `styles.css`: responsive styling
- `app.js`: search and empty state
- `assets/logo.svg`: local logo
- `tests/app.test.js`: eight unit tests using Node's built-in test runner
- `server.js`: local static server using Node's built-in modules
- `package.json`: start and test commands; no external dependencies

Run tests from this folder with Node.js: `npm test` (or `node --test`).
No package installation is needed.
