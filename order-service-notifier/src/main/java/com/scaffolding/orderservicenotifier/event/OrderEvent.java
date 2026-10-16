package com.scaffolding.orderservicenotifier.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OrderEvent(
        UUID eventId,
        OrderEventType eventType,
        int schemaVersion,
        Instant occurredAt,
        UUID orderId,
        OrderEventPayload payload) {

    public OrderEvent {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(occurredAt, "occurredAt");
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(payload, "payload");
        if (schemaVersion != 1 || eventType != payload.eventType()) {
            throw new IllegalArgumentException("Event type and version must match the payload schema");
        }
    }
}
