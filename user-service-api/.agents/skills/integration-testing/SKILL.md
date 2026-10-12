---
name: integration-testing
description: Add or update Spring integration tests in user-service-api, following the existing API and persistence test conventions.
---

# Integration Testing

- Read the affected code and existing tests first. Use integration tests to check behavior across the real application wiring, HTTP layer, and persistence.
- Follow `UserApiIntegrationTest`: use JUnit Jupiter, `@SpringBootTest`, and `@AutoConfigureMockMvc`; exercise endpoints with `MockMvc` and assert response status, headers, and JSON fields.
- Use the in-memory H2 database and repository assertions to confirm persisted state. Clear data before each test and keep test inputs deterministic.
- Cover relevant CRUD behavior and validation or error responses. Assert both the HTTP result and important database side effects.
- Run the focused test with `./mvnw -Dtest=ClassNameIntegrationTest test`; run the full suite with `./mvnw test` when broader behavior is affected.
