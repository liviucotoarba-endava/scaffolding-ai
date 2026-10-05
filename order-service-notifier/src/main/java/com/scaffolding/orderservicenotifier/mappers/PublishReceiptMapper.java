package com.scaffolding.orderservicenotifier.mappers;

import com.scaffolding.orderservicenotifier.event.OrderEvent;
import com.scaffolding.orderservicenotifier.producer.PublishReceipt;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfiguration.class)
public interface PublishReceiptMapper {

    @Mapping(target = "eventId", source = "event.eventId")
    @Mapping(target = "topic", expression = "java(metadata.topic())")
    @Mapping(target = "partition", expression = "java(metadata.partition())")
    @Mapping(target = "offset", expression = "java(metadata.offset())")
    PublishReceipt toReceipt(OrderEvent event, RecordMetadata metadata);
}
