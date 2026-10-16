package com.scaffolding.orderservicenotifier.mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.scaffolding.orderservicenotifier.api.OrderEventRequests;
import com.scaffolding.orderservicenotifier.event.OrderEvent;
import com.scaffolding.orderservicenotifier.event.OrderEventPayload;
import com.scaffolding.orderservicenotifier.event.OrderEventType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEventMapperTest {

    private final OrderEventMapper mapper = Mappers.getMapper(OrderEventMapper.class);
    private final UUID orderId = UUID.randomUUID();

    @Test
    void mapsCreatedRequestAndGeneratesEnvelopeMetadata() {
        var userId = UUID.randomUUID();
        var amount = new BigDecimal("49.99");
        var request = new OrderEventRequests.Created(orderId, userId, amount, "EUR");
        var before = Instant.now();

        var event = mapper.toEvent(request);

        assertEnvelope(event, OrderEventType.ORDER_CREATED);
        assertThat(event.occurredAt()).isBetween(before, Instant.now());
        assertThat(event.payload()).isEqualTo(new OrderEventPayload.Created(userId, amount, "EUR"));
        assertThat(mapper.toEvent(request).eventId()).isNotEqualTo(event.eventId());
    }

    @Test
    void mapsPaidRequestWithoutLosingPaymentFields() {
        var amount = new BigDecimal("49.99");

        var event = mapper.toEvent(new OrderEventRequests.Paid(orderId, "payment-001", amount, "EUR"));

        assertEnvelope(event, OrderEventType.ORDER_PAID);
        assertThat(event.payload()).isEqualTo(new OrderEventPayload.Paid("payment-001", amount, "EUR"));
    }

    @Test
    void mapsCancelledRequestWithoutLosingReason() {
        var event = mapper.toEvent(new OrderEventRequests.Cancelled(orderId, "Customer changed their mind"));

        assertEnvelope(event, OrderEventType.ORDER_CANCELLED);
        assertThat(event.payload()).isEqualTo(new OrderEventPayload.Cancelled("Customer changed their mind"));
    }

    private void assertEnvelope(OrderEvent event, OrderEventType expectedType) {
        assertThat(event.eventId()).isNotNull();
        assertThat(event.orderId()).isEqualTo(orderId);
        assertThat(event.eventType()).isEqualTo(expectedType);
        assertThat(event.schemaVersion()).isEqualTo(1);
        assertThat(event.occurredAt()).isNotNull();
    }
}
