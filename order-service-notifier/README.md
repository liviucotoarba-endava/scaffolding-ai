# Order Service Notifier

A small Kafka producer example using Java 25, Spring Boot 4.0.8, Spring MVC, and Spring for Apache Kafka. It publishes mock order lifecycle events through manual HTTP triggers. MapStruct 1.6.3 generates request-to-event and acknowledgement-to-receipt mappings; Lombok generates dependency constructors. There is no database, consumer, connection to `user-service-api`, or automatic publishing at startup.

## Build

Use a JDK 25 installation:

```sh
./mvnw clean verify
```

On Windows, use `mvnw.cmd`. The wrapper downloads Maven 3.9.11 on first use. Building requires dependency downloads but does not require Kafka. The executable JAR is `target/order-service-notifier-0.0.1-SNAPSHOT.jar`. To package without running tests, use `./mvnw -DskipTests clean package`.

## Tests and mappings

JUnit Jupiter and Mockito are supplied by Spring Boot's MVC test starter. Run all tests with `./mvnw test` or include them in the packaged build with `./mvnw clean verify`.

- Unit tests verify all three event mappings, record keys and headers, acknowledgement receipts, asynchronous and immediate failures, blocking timeout and interruption behavior, and sequential batch publication.
- Three minimal `@SpringBootTest` / MockMvc integration tests exercise the full application context, HTTP binding, generated MapStruct beans, the producer, and response/error handling. They mock only `KafkaTemplate` and disable topic auto-creation, so no broker or Docker is needed. They do not verify actual Kafka transport.

The shared MapStruct configuration registers Spring beans and treats unmapped target fields as compilation errors. Controllers and the demo factory use the same generated event mapper; the producer uses a generated receipt mapper. Kafka record construction and header encoding remain in the producer. Mockito is configured as a JVM agent for tests on Java 25.

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

The notifier keeps Kafka for publishing; the user API keeps JPA and H2 for persistence. No shared parent project or additional test infrastructure is needed.

## What is being simulated

Kafka stores messages in named **topics**. A **producer** publishes messages; a **consumer** reads them independently. This service simulates order activity and sends real Kafka records through Spring's `KafkaTemplate`.

All events go to `orders.events.v1`. The order ID is the record **key**, so events for the same order go to the same partition while the topic's partition count stays fixed. Kafka preserves order within a partition. Each message contains an event ID, event type, schema version, occurrence time, order ID, and typed payload. The record also carries UTF-8 `event-id`, `event-type`, and `schema-version` headers. Java class name headers are disabled so consumers can use the JSON contract without depending on this project's classes.

| Event type | Payload fields |
| --- | --- |
| `ORDER_CREATED` | `userId`, `totalAmount`, `currency` |
| `ORDER_PAID` | `paymentReference`, `paidAmount`, `currency` |
| `ORDER_CANCELLED` | `reason` |

Example message:

```json
{
  "eventId": "b54dfb2f-cc7b-44a8-b513-9b5971c7d947",
  "eventType": "ORDER_CREATED",
  "schemaVersion": 1,
  "occurredAt": "2026-10-05T12:00:00Z",
  "orderId": "dc39c3c0-3d36-4eb0-9f4b-fae0331c1a05",
  "payload": {
    "userId": "12d7dd87-64de-4633-b9b2-3a858ba1d101",
    "totalAmount": 49.99,
    "currency": "EUR"
  }
}
```

## Publishing mechanisms

- **Asynchronous:** `OrderEventProducer.publish` returns a `CompletableFuture` and processes Kafka's acknowledgement or failure. HTTP uses this mode by default and completes the response after acknowledgement.
- **Blocking:** `publishBlocking` waits for acknowledgement with a configurable timeout and restores the thread's interrupted status if interrupted. Select it with `?mode=BLOCKING` on an individual event endpoint.
- **Batch:** `publishBatch` sends a list sequentially, waiting for each acknowledgement before sending the next. The demo endpoint generates four events in memory: create/pay for one order and create/cancel for another. This is an application batch, not a Kafka transaction; an acknowledged prefix remains published if a later event fails.

Successful requests return HTTP 200 with Kafka's topic, partition, offset, and the event ID; the demo endpoint returns a list of those receipts. Invalid bodies or modes return HTTP 400. Publication failures return HTTP 503, and a blocking acknowledgement timeout returns HTTP 504, using Spring's `ProblemDetail` response format.

The producer uses `acks=all`, idempotence, Kafka-managed retries, LZ4 compression, and a small linger interval. Idempotence prevents duplicates from the producer's internal retries; it does not deduplicate repeated HTTP requests. Every trigger creates a fresh event ID. A timeout or connection failure can leave delivery uncertain, so a caller retry may publish another event. The example does not enforce order state transitions or implement an outbox.

## Optional manual use

A real Kafka broker is required only if you choose to run and publish. The application declares the topic with three partitions and one replica for a local single-broker setup. Startup fails if Kafka cannot be reached. Use a higher replica count with a suitable cluster if needed; topic permissions must allow creation or the topic must already exist.

```sh
KAFKA_BOOTSTRAP_SERVERS=localhost:9092 ./mvnw spring-boot:run
```

The default HTTP port is **8081**, so this can sit beside the user service on 8080. Available configuration:

| Environment variable | Default |
| --- | --- |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` |
| `SERVER_PORT` | `8081` |
| `ORDER_EVENTS_TOPIC` | `orders.events.v1` |
| `ORDER_EVENTS_PARTITIONS` | `3` |
| `ORDER_EVENTS_REPLICAS` | `1` |

Other Spring Kafka settings and `app.events.publish-timeout` can be overridden with standard Spring configuration. Amounts are positive decimals with up to two decimal places; currency is a three-letter uppercase code shape. IDs are UUIDs and are illustrative rather than looked up in the user service.

Create an order event asynchronously:

```sh
curl -X POST http://localhost:8081/api/order-events/created \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"dc39c3c0-3d36-4eb0-9f4b-fae0331c1a05","userId":"12d7dd87-64de-4633-b9b2-3a858ba1d101","totalAmount":49.99,"currency":"EUR"}'
```

Publish a payment and wait on the request thread:

```sh
curl -X POST 'http://localhost:8081/api/order-events/paid?mode=BLOCKING' \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"dc39c3c0-3d36-4eb0-9f4b-fae0331c1a05","paymentReference":"payment-001","paidAmount":49.99,"currency":"EUR"}'
```

Publish a cancellation:

```sh
curl -X POST http://localhost:8081/api/order-events/cancelled \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"dc39c3c0-3d36-4eb0-9f4b-fae0331c1a05","reason":"Customer changed their mind"}'
```

Publish the generated sample batch:

```sh
curl -X POST http://localhost:8081/api/order-events/demo-batch
```

## Code layout

- `event`: immutable event envelope and typed payloads.
- `producer`: Kafka records, headers, publication modes, receipts, and failure handling.
- `api`: validated request bodies, manual triggers, and publication error responses.
- `config`: topic declaration and application properties.
- `demo`: in-memory sample event generation.
- `mappers`: MapStruct configuration and generated object mapping contracts.
- `src/test/java`: JUnit/Mockito unit tests and minimal Spring Boot integration tests.

References: [Spring Boot Java compatibility](https://docs.spring.io/spring-boot/4.0/system-requirements.html), [Kafka auto-configuration](https://docs.spring.io/spring-boot/4.0/reference/messaging/kafka.html), [KafkaTemplate publishing](https://docs.spring.io/spring-kafka/reference/kafka/sending-messages.html), [JSON serialization](https://docs.spring.io/spring-kafka/reference/kafka/serdes.html), [MapStruct](https://mapstruct.org/documentation/1.6/reference/html/), and [Spring Boot testing](https://docs.spring.io/spring-boot/4.0/reference/testing/index.html).
