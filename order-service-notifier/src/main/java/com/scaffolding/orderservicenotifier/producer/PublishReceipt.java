package com.scaffolding.orderservicenotifier.producer;

import java.util.UUID;

public record PublishReceipt(UUID eventId, String topic, int partition, long offset) {
}
