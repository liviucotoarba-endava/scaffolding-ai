package com.scaffolding.orderservicenotifier.producer;

import java.util.UUID;

public class EventPublishException extends RuntimeException {

    private final UUID eventId;

    public EventPublishException(UUID eventId, String message, Throwable cause) {
        super(message, cause);
        this.eventId = eventId;
    }

    public UUID eventId() {
        return eventId;
    }
}
