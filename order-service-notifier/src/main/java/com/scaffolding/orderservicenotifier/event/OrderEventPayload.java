package com.scaffolding.orderservicenotifier.event;

import java.math.BigDecimal;
import java.util.UUID;

public sealed interface OrderEventPayload {

    OrderEventType eventType();

    record Created(UUID userId, BigDecimal totalAmount, String currency) implements OrderEventPayload {
        @Override
        public OrderEventType eventType() {
            return OrderEventType.ORDER_CREATED;
        }
    }

    record Paid(String paymentReference, BigDecimal paidAmount, String currency) implements OrderEventPayload {
        @Override
        public OrderEventType eventType() {
            return OrderEventType.ORDER_PAID;
        }
    }

    record Cancelled(String reason) implements OrderEventPayload {
        @Override
        public OrderEventType eventType() {
            return OrderEventType.ORDER_CANCELLED;
        }
    }
}
