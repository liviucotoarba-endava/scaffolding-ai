# Order Service Notifier

Minimal Kafka producer for order lifecycle events, triggered through HTTP.

## Stack

- Java 25, Spring Boot 4, Maven
- Spring MVC, Spring Kafka, Jakarta Validation, and MapStruct
- JSON event envelopes for `ORDER_CREATED`, `ORDER_PAID`, and `ORDER_CANCELLED`

## Run

Start a Kafka broker reachable at `localhost:9092`, then run from this folder:

```sh
./mvnw spring-boot:run
```

The API listens on port `8081`. Override `KAFKA_BOOTSTRAP_SERVERS`, `SERVER_PORT`, `ORDER_EVENTS_TOPIC`, `ORDER_EVENTS_PARTITIONS`, or `ORDER_EVENTS_REPLICAS` through environment variables as needed. Defaults are topic `orders.events.v1`, three partitions, and one replica.

## Endpoints

| Method | Path | Behavior |
| --- | --- | --- |
| POST | `/api/order-events/created` | Publish an order-created event |
| POST | `/api/order-events/paid` | Publish an order-paid event |
| POST | `/api/order-events/cancelled` | Publish an order-cancelled event |
| POST | `/api/order-events/demo-batch` | Publish a sample created/paid and created/cancelled sequence |

Requests and responses use JSON. The first three endpoints accept `?mode=ASYNC` (default) or `?mode=BLOCKING`. Both return `200` with a receipt containing `eventId`, `topic`, `partition`, and `offset` after Kafka acknowledges the event. Invalid requests return `400`; publication failures return `503`; a blocking acknowledgement timeout returns `504` with delivery unknown.

For example, with the application and broker running:

```sh
curl -X POST 'http://localhost:8081/api/order-events/created' \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"dc39c3c0-3d36-4eb0-9f4b-fae0331c1a05","userId":"12d7dd87-64de-4633-b9b2-3a858ba1d101","totalAmount":49.99,"currency":"EUR"}'
```

Events use the order ID as their Kafka key and carry `event-id`, `event-type`, and `schema-version` headers. Batches wait for each acknowledgement before sending the next event; a failed batch can leave earlier events published. Producer idempotence does not deduplicate repeated HTTP requests.

## Test

```sh
./mvnw test
```

Tests run without a Kafka broker. Producer unit tests mock `KafkaTemplate`; HTTP integration tests use the real Spring wiring and mappers with a mocked template and Kafka topic auto-creation disabled.

## Codex

Open this folder as the Codex project, or launch from the repository root:

```sh
codex --cd order-service-notifier
```

The local scaffold mirrors User Service API's tool categories, with Kafka-specific guidance:

| Tool | Location / purpose |
| --- | --- |
| Repository instructions | [AGENTS.md](AGENTS.md): architecture, event contracts, checks, and Git conventions |
| Bug fixing | [.agents/skills/bug-fixing](.agents/skills/bug-fixing/SKILL.md): reproduce and verify a focused fix |
| Unit testing | [.agents/skills/unit-testing](.agents/skills/unit-testing/SKILL.md): shared Java testing workflow; producer-specific setup in `AGENTS.md` |
| Integration testing | [.agents/skills/integration-testing](.agents/skills/integration-testing/SKILL.md): shared Spring HTTP testing workflow; Kafka isolation in `AGENTS.md` |
| MapStruct mapping | [.agents/skills/mapstruct-mapping](.agents/skills/mapstruct-mapping/SKILL.md): shared mapping conventions, applied to this service's event and receipt mappings |
| Jira tickets | [.agents/skills/create-jira-ticket](.agents/skills/create-jira-ticket/SKILL.md): shared ticket drafting workflow and reusable examples |
| Remote tools | Skills for [GitHub](.agents/skills/github/SKILL.md), [GitLab](.agents/skills/gitlab/SKILL.md), [Jenkins](.agents/skills/jenkins/SKILL.md), and [Sonar](.agents/skills/sonar/SKILL.md) |
| Implementation agent | [.codex/agents/kafka-event-implementer.toml](.codex/agents/kafka-event-implementer.toml): implement and verify Kafka event changes |
| Atlassian MCP | [.codex/config.toml](.codex/config.toml): same server configuration as User Service API; authenticate before use |
| Commit hook | [.codex/hooks.json](.codex/hooks.json) and [.codex/hooks](.codex/hooks): check Conventional Commit subjects for simple `Bash` Git commit/push commands |

The remote-tool workflows are copied from User Service API. The unit testing, integration testing, and MapStruct skills, plus the complete Jira skill folder including examples, are identical in both services. Each service's `AGENTS.md` preserves its specific testing setup: H2 persistence for the users API and mocked Kafka publication for the notifier. Repository instructions and the implementation agent carry the local domain guidance.

The hook uses the notifier's path within this shared Git repository and compares pushed subjects with local remote-tracking refs. It does not grant commit or push approval. Inspect and trust the new hook in Codex before relying on it, as described in the [official hook documentation](https://learn.chatgpt.com/docs/hooks). The agent follows the [custom agent format](https://learn.chatgpt.com/docs/agent-configuration/subagents).

The CLI skills rely on existing local installations and authentication. The copied GitLab skill records that `glab` is unavailable on this Mac. Copying these files does not install tools or authenticate remote services.

### Presentation prompts

- `Use $unit-testing to cover the producer's batch failure behavior. Keep the tests broker-free.`
- `Use $integration-testing to add HTTP coverage for /api/order-events/paid, including invalid input and publication failure.`
- `Use the kafka-event-implementer agent to add ORDER_SHIPPED and a /api/order-events/shipped trigger, following the current envelope, headers, and tests.`
- `Use $create-jira-ticket to draft a story for ORDER_SHIPPED. Return the draft for review.`

### Check the scaffold

```sh
python3 -B -m unittest discover -s .codex/hooks -p 'test_*.py'
```
