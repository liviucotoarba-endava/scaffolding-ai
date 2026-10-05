# user-service-api

Minimal REST API to create, read, replace, and delete users (`id`, `name`, `email`).
Stack: Java 25, Spring Boot 4.0.8, Hibernate/JPA, H2 in memory, Lombok, MapStruct 1.6.3.
Packages under `com.scaffolding.userserviceapi`: `controllers`, `services`, `repos`, `entities`, `dto`, `mappers`, `exceptions`.

Run with Java 25: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
Base URL: `http://localhost:8080/api/users`. The Maven wrapper downloads Maven on first use.

| Method | Path | Result |
| --- | --- | --- |
| POST | `/api/users` | Create a user; 201 with DTO and Location header |
| GET | `/api/users` | List users; 200 with DTOs |
| GET | `/api/users/{id}` | Read a user; 200 with DTO |
| PUT | `/api/users/{id}` | Replace a user's name and email; 200 with DTO |
| DELETE | `/api/users/{id}` | Delete a user; 204 |

POST/PUT JSON: `{"name":"Ada Lovelace","email":"ada@example.com"}` (`Content-Type: application/json`).
Flow: controller → service → repository → entity; MapStruct maps DTOs. Invalid input returns 400; missing users return 404.
Data resets on restart. `./mvnw test` runs JUnit/Mockito unit tests and five Spring Boot/H2 integration tests; `./mvnw package` builds the app. No PATCH endpoint.

## Shared Java conventions

Both Java demos use the same baseline:

- Java 25, Spring Boot 4.0.8, Maven wrapper 3.9.11, and MapStruct 1.6.3.
- Spring MVC and Validation starters; Lombok and test library versions managed by Spring Boot.
- Records for request/response data and Lombok `@RequiredArgsConstructor` for constructor injection.
- `mappers/MappingConfiguration`: Spring beans, constructor injection, and compilation errors for unmapped target fields. Mappers use `@Mapper(config = MappingConfiguration.class)`.
- The same compiler processors for Lombok, `lombok-mapstruct-binding` 0.2.0, and MapStruct.
- JUnit Jupiter, AssertJ assertions, JUnit `assertThrows`, and Mockito. Unit tests use `*Test`, construct the subject directly, and use `@ExtendWith(MockitoExtension.class)` when mocks are needed. Mapper tests use the generated implementation through `Mappers.getMapper`.
- HTTP integration tests use `*IntegrationTest`, `@SpringBootTest`, and `@AutoConfigureMockMvc`. They exercise real application wiring and mock external infrastructure only when needed.
- `./mvnw test` runs unit and integration tests; `./mvnw clean verify` also packages the app. Mockito runs as a JVM agent on Java 25.

This API keeps JPA and H2 for persistence; the notifier keeps Kafka for publishing. No shared parent project or additional test infrastructure is needed.
