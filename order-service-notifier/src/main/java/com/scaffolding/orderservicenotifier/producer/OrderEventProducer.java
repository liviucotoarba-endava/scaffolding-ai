package com.scaffolding.orderservicenotifier.producer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.scaffolding.orderservicenotifier.config.OrderEventProperties;
import com.scaffolding.orderservicenotifier.event.OrderEvent;
import com.scaffolding.orderservicenotifier.mappers.PublishReceiptMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final OrderEventProperties properties;
    private final PublishReceiptMapper receiptMapper;

    public CompletableFuture<PublishReceipt> publish(OrderEvent event) {
        var record = new ProducerRecord<String, OrderEvent>(
                properties.topic(), null, event.occurredAt().toEpochMilli(), event.orderId().toString(), event);
        record.headers().add("event-id", bytes(event.eventId().toString()));
        record.headers().add("event-type", bytes(event.eventType().name()));
        record.headers().add("schema-version", bytes(Integer.toString(event.schemaVersion())));

        try {
            return kafkaTemplate.send(record).handle((result, failure) -> {
                if (failure != null) {
                    throw publishFailure(event, failure);
                }
                var metadata = result.getRecordMetadata();
                log.info("Published {} eventId={} orderId={} topic={} partition={} offset={}",
                        event.eventType(), event.eventId(), event.orderId(),
                        metadata.topic(), metadata.partition(), metadata.offset());
                return receiptMapper.toReceipt(event, metadata);
            });
        } catch (RuntimeException failure) {
            // Serialization or metadata lookup can fail before KafkaTemplate returns a future.
            return CompletableFuture.failedFuture(publishFailure(event, failure));
        }
    }

    public PublishReceipt publishBlocking(OrderEvent event) {
        try {
            return publish(event).get(properties.publishTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            throw new EventPublishException(event.eventId(), "Interrupted while waiting for Kafka acknowledgement", failure);
        } catch (TimeoutException failure) {
            // A timeout does not cancel the underlying send; Kafka may still receive the event.
            throw new EventPublishException(event.eventId(), "Kafka acknowledgement timed out; delivery is unknown", failure);
        } catch (ExecutionException failure) {
            if (failure.getCause() instanceof EventPublishException publishException) {
                throw publishException;
            }
            throw publishFailure(event, failure.getCause());
        }
    }

    public CompletableFuture<List<PublishReceipt>> publishBatch(List<OrderEvent> events) {
        CompletableFuture<List<PublishReceipt>> result = CompletableFuture.completedFuture(new ArrayList<>());
        for (var event : List.copyOf(events)) {
            // Wait for each acknowledgement before sending the next event. This batch is not a transaction.
            result = result.thenCompose(receipts -> publish(event).thenApply(receipt -> {
                receipts.add(receipt);
                return receipts;
            }));
        }
        return result.thenApply(List::copyOf);
    }

    private EventPublishException publishFailure(OrderEvent event, Throwable failure) {
        log.error("Failed to publish {} eventId={} orderId={}",
                event.eventType(), event.eventId(), event.orderId(), failure);
        return new EventPublishException(event.eventId(), "Kafka could not confirm event delivery", failure);
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
