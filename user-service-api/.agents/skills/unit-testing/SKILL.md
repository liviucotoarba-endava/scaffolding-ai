---
name: unit-testing
description: Add or update unit tests in user-service-api, following the existing Java and Spring test conventions.
---

# Unit Testing

- Read the affected code and nearby tests first. Keep each test focused on observable behavior, including relevant boundary and failure cases.
- Use JUnit Jupiter with AssertJ for assertions, `assertThrows` for exceptions, and Mockito for collaborators. Construct the class under test directly; use `MockitoExtension` when mocks are needed.
- Test mappers with `Mappers.getMapper` and DTO constraints with Jakarta Validation. For controllers, use standalone `MockMvc`; reserve `@SpringBootTest` and `@AutoConfigureMockMvc` for integration coverage with application wiring and persistence.
- Keep test data deterministic and verify important interactions or absence of side effects where relevant.
- Run focused tests with `./mvnw -Dtest=ClassNameTest test`; run the full suite with `./mvnw test` when the change affects broader behavior.
