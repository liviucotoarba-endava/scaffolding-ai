package com.scaffolding.orderservicenotifier.demo;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.scaffolding.orderservicenotifier.api.OrderEventRequests;
import com.scaffolding.orderservicenotifier.event.OrderEvent;
import com.scaffolding.orderservicenotifier.mappers.OrderEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoEventFactory {

    private final OrderEventMapper eventMapper;

    public List<OrderEvent> createBatch() {
        var paidOrderId = UUID.randomUUID();
        var cancelledOrderId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        return List.of(
                eventMapper.toEvent(new OrderEventRequests.Created(paidOrderId, userId, new BigDecimal("49.99"), "EUR")),
                eventMapper.toEvent(new OrderEventRequests.Paid(paidOrderId, "demo-payment-" + paidOrderId, new BigDecimal("49.99"), "EUR")),
                eventMapper.toEvent(new OrderEventRequests.Created(cancelledOrderId, userId, new BigDecimal("19.99"), "EUR")),
                eventMapper.toEvent(new OrderEventRequests.Cancelled(cancelledOrderId, "Customer changed their mind")));
    }
}
