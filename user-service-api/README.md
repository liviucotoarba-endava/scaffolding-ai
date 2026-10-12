# User Service API

Minimal REST API for managing users.

## Stack
- Java 25, Spring Boot 4, Maven
- Spring MVC, Spring Data JPA, Jakarta Validation, and MapStruct
- H2 in-memory database

## Run

```sh
./mvnw spring-boot:run
```

## Endpoints
| Method | Path | Behavior |
| --- | --- | --- |
| GET | `/api/users` | List users |
| GET | `/api/users/{id}` | Get a user |
| POST | `/api/users` | Create a user (201) |
| PUT | `/api/users/{id}` | Replace user details |
| DELETE | `/api/users/{id}` | Delete a user (204) |

Requests and responses use JSON. Invalid requests return 400; missing users return 404.

## Test
```sh
./mvnw test
```
