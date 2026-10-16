# Repository Guidance

## Project
This is a Java 25 Spring Boot REST API. Use the Maven wrapper for build tasks.

## Development
- Follow existing controller, service, repository, DTO, and mapper patterns; keep changes focused.
- Add or update tests for behavior changes. Run focused checks first, then broader checks as appropriate.
- Use `./mvnw test` for the full suite and report checks and failures accurately.
- Preserve unrelated user changes and review your diff before finishing.

## Testing
- Follow `UserServiceTest` for service unit tests: construct the service directly with mocked repository and mapper collaborators. Use standalone `MockMvc` for controller unit tests, Jakarta Validation for DTO constraints, and the real generated mapper for mapper tests.
- Follow `UserApiIntegrationTest`: use `@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:users-integration;DB_CLOSE_DELAY=-1")` and `@AutoConfigureMockMvc` with the real repository and in-memory H2 database.
- Clear repository data before each integration test. Verify persisted CRUD state alongside HTTP status, Location headers where relevant, and JSON fields; cover validation and not-found responses when affected.
- Use the shared `unit-testing` and `integration-testing` skills in `.agents/skills` for the common workflow.

## Git and Remote Access
- Never commit or push automatically. Get specific user approval before each commit or push.
- For GitHub, GitLab, Jenkins, and other remote systems, use the configured local CLI and default to read-only commands.
- Get approval before remote writes, including creating, editing, commenting on, or merging work, and before triggering or changing jobs.
- Never print, copy, or expose credentials, tokens, or secret values.

## Branches and Commits
- Keep changes small and integrate frequently with `main`; use short-lived `codex/` branches when branching, not long-lived branches.
- Use lowercase Conventional Commit subjects: `type(scope): description` (for example, `fix(users): handle missing user`). Add `!` before the colon for breaking changes.
