---
name: integration-testing-java
description: Add or update Spring HTTP integration tests using the project's application wiring, dependency setup, and isolation conventions.
---

# Integration Testing

- Read the repository instructions, affected code, and existing integration tests first. Check behavior across the real application wiring and HTTP layer using the dependency setup documented in `AGENTS.md`.
- Use JUnit Jupiter, `@SpringBootTest`, and `@AutoConfigureMockMvc`; exercise endpoints with `MockMvc` and assert relevant response status, headers, and JSON fields.
- Follow the project's conventions for test configuration and external dependencies. Keep test inputs deterministic, reset shared state between tests, and accurately report which dependencies are real or mocked.
- Assert both the HTTP result and important state changes or collaborator interactions. Cover relevant successful behavior, validation, and error responses, including absence of side effects for rejected requests.
- For asynchronous HTTP processing, assert `asyncStarted()` and use `asyncDispatch(result)` before checking the final response.
- Run the focused test with `./mvnw -Dtest=ClassNameIntegrationTest test`, replacing `ClassNameIntegrationTest` with the actual test class; run the full suite with `./mvnw test` when broader behavior is affected.
