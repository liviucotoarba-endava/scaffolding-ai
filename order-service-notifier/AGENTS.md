# Repository Guidance

## Project
This is a Java 25 Spring Boot Kafka producer with manual HTTP triggers. Use the Maven wrapper for build tasks. It publishes order lifecycle events; it has no database or Kafka consumer.

## Development
- Follow existing API, event, producer, configuration, demo, and mapper patterns; keep changes focused.
- Keep HTTP validation in `api`, event contracts in `event`, publishing in `producer`, and request/event and acknowledgement/receipt conversions in `mappers`.
- Preserve the order ID as the Kafka record key, envelope metadata, and `event-id`, `event-type`, and `schema-version` headers. Keep event types and payloads consistent with the schema version.
- Return publish receipts only after acknowledgement. A blocking timeout leaves delivery unknown; batches publish sequentially and can partially succeed.
- Add or update tests for behavior changes. Run focused checks first, then broader checks as appropriate.
- Use `./mvnw test` for the full suite and report checks and failures accurately. Existing tests mock `KafkaTemplate` and run without a broker; keep that default.
- Preserve unrelated user changes and review your diff before finishing.

## Testing
- Follow `OrderEventProducerTest` for unit tests: mock `KafkaTemplate<String, OrderEvent>`, construct the producer directly with `OrderEventProperties` and the real receipt mapper, and control sends with completed, failed, or pending `CompletableFuture` values.
- Capture records to check topic, order ID key, timestamp, headers, payload, and acknowledgement receipts. Cover immediate and asynchronous failures, blocking timeout and interruption, and batch ordering or partial failure when relevant. Control future completion without sleeps; clear interrupted status in a `finally` block.
- Follow `OrderEventHttpIntegrationTest`: use `@SpringBootTest(properties = "spring.kafka.admin.auto-create=false")`, `@AutoConfigureMockMvc`, and `@MockitoBean` for the Kafka template. Keep the real application wiring and mappers; the suite needs no broker.
- Stub sends with deterministic `SendResult` metadata and capture published records. Dispatch asynchronous HTTP results before checking receipts or errors; cover the publish modes when relevant, validation without publication (`400`), publication failures (`503`), and blocking timeout responses (`504`).
- Use the shared `unit-testing` and `integration-testing` skills in `.agents/skills` for the common workflow.

## Local AI Tools
- Read the relevant workflow in `.agents/skills`: `bug-fixing`, `unit-testing`, `integration-testing`, or `mapstruct-mapping` for development; `create-jira-ticket` for ticket drafts; `github`, `gitlab`, `jenkins`, or `sonar` for remote tooling.
- Use `.codex/agents/kafka-event-implementer.toml` when the user requests the Kafka implementation agent.
- `.codex/config.toml` carries the Atlassian MCP configuration. `.codex/hooks.json` checks Conventional Commit subjects for supported Codex-issued Git commands.
- Check the hook with `python3 -B -m unittest discover -s .codex/hooks -p 'test_*.py'` after changing it.

## Git and Remote Access
- Never commit or push automatically. Get specific user approval before each commit or push.
- For GitHub, GitLab, Jenkins, and other remote systems, use the configured local CLI and default to read-only commands.
- Get approval before remote writes, including creating, editing, commenting on, or merging work, and before triggering or changing jobs.
- Never print, copy, or expose credentials, tokens, or secret values.

## Branches and Commits
- Keep changes small and integrate frequently with `main`; use short-lived `codex/` branches when branching, not long-lived branches.
- Use lowercase Conventional Commit subjects: `type(scope): description` (for example, `fix(orders): preserve publish failure details`). Add `!` before the colon for breaking changes.
