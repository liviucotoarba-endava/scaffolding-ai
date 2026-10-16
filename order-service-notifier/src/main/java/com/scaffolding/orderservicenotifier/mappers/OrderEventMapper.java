package com.scaffolding.orderservicenotifier.mappers;

import java.time.Instant;
import java.util.UUID;

import com.scaffolding.orderservicenotifier.api.OrderEventRequests;
import com.scaffolding.orderservicenotifier.event.OrderEvent;
import com.scaffolding.orderservicenotifier.event.OrderEventPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfiguration.class, imports = {Instant.class, UUID.class})
public interface OrderEventMapper {

    @Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "eventType", constant = "ORDER_CREATED")
    @Mapping(target = "schemaVersion", constant = "1")
    @Mapping(target = "occurredAt", expression = "java(Instant.now())")
    @Mapping(target = "payload", source = "request")
    OrderEvent toEvent(OrderEventRequests.Created request);

    @Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "eventType", constant = "ORDER_PAID")
    @Mapping(target = "schemaVersion", constant = "1")
    @Mapping(target = "occurredAt", expression = "java(Instant.now())")
    @Mapping(target = "payload", source = "request")
    OrderEvent toEvent(OrderEventRequests.Paid request);

    @Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "eventType", constant = "ORDER_CANCELLED")
    @Mapping(target = "schemaVersion", constant = "1")
    @Mapping(target = "occurredAt", expression = "java(Instant.now())")
    @Mapping(target = "payload", source = "request")
    OrderEvent toEvent(OrderEventRequests.Cancelled request);

    OrderEventPayload.Created toPayload(OrderEventRequests.Created request);

    OrderEventPayload.Paid toPayload(OrderEventRequests.Paid request);

    OrderEventPayload.Cancelled toPayload(OrderEventRequests.Cancelled request);
}
