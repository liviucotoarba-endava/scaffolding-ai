package com.scaffolding.orderservicenotifier.producer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import com.scaffolding.orderservicenotifier.api.PublishMode;
import com.scaffolding.orderservicenotifier.config.OrderEventProperties;
import com.scaffolding.orderservicenotifier.event.OrderEvent;
import com.scaffolding.orderservicenotifier.event.OrderEventPayload;
import com.scaffolding.orderservicenotifier.event.OrderEventType;
import com.scaffolding.orderservicenotifier.mappers.PublishReceiptMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderEventProducerTest {

    private static final String TOPIC = "orders.events.v1";

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Captor
    private ArgumentCaptor<ProducerRecord<String, OrderEvent>> recordCaptor;

    private OrderEventProducer producer;

    @BeforeEach
    void setUp() {
        producer = producerWithTimeout(Duration.ofSeconds(1));
    }

    @ParameterizedTest
    @EnumSource(PublishMode.class)
    void publishesKeyedRecordWithHeadersAndMapsAcknowledgement(PublishMode mode) {
        var event = event();
        when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, OrderEvent>>any()))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(acknowledged(invocation.getArgument(0))));

        var receipt = mode == PublishMode.ASYNC ? producer.publish(event).join() : producer.publishBlocking(event);

        verify(kafkaTemplate).send(recordCaptor.capture());
        var record = recordCaptor.getValue();
        assertThat(record.topic()).isEqualTo(TOPIC);
        assertThat(record.key()).isEqualTo(event.orderId().toString());
        assertThat(record.timestamp()).isEqualTo(event.occurredAt().toEpochMilli());
        assertThat(record.value()).isSameAs(event);
        assertThat(header(record, "event-id")).isEqualTo(event.eventId().toString());
        assertThat(header(record, "event-type")).isEqualTo("ORDER_CANCELLED");
        assertThat(header(record, "schema-version")).isEqualTo("1");
        assertThat(receipt).isEqualTo(new PublishReceipt(event.eventId(), TOPIC, 2, 42));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void exposesImmediateAndAsynchronousFailuresAsFailedFutures(boolean immediateFailure) {
        var event = event();
        var failure = new KafkaException("Broker unavailable");
        var send = when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, OrderEvent>>any()));
        if (immediateFailure) {
            send.thenThrow(failure);
        } else {
            send.thenReturn(CompletableFuture.failedFuture(failure));
        }

        var completion = assertThrows(CompletionException.class, () -> producer.publish(event).join());

        assertThat(completion.getCause()).isInstanceOf(EventPublishException.class);
        var exception = (EventPublishException) completion.getCause();
        assertThat(exception.eventId()).isEqualTo(event.eventId());
        assertThat(exception.getCause()).isSameAs(failure);
    }

    @Test
    void blockingTimeoutLeavesUnderlyingSendPending() {
        var pendingSend = new CompletableFuture<SendResult<String, OrderEvent>>();
        when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, OrderEvent>>any())).thenReturn(pendingSend);
        var event = event();

        var exception = assertThrows(EventPublishException.class,
                () -> producerWithTimeout(Duration.ofMillis(1)).publishBlocking(event));

        assertThat(exception.eventId()).isEqualTo(event.eventId());
        assertThat(exception.getCause()).isInstanceOf(TimeoutException.class);
        assertThat(pendingSend).isNotDone();
    }

    @Test
    void blockingPublicationRestoresInterruptedStatus() {
        when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, OrderEvent>>any()))
                .thenReturn(new CompletableFuture<>());

        Thread.currentThread().interrupt();
        try {
            var exception = assertThrows(EventPublishException.class, () -> producer.publishBlocking(event()));

            assertThat(exception.getCause()).isInstanceOf(InterruptedException.class);
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void batchWaitsForEachAcknowledgementBeforeSendingNextEvent() {
        var first = new CompletableFuture<SendResult<String, OrderEvent>>();
        var second = new CompletableFuture<SendResult<String, OrderEvent>>();
        when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, OrderEvent>>any()))
                .thenReturn(first).thenReturn(second);
        var events = List.of(event(), event());

        var result = producer.publishBatch(events);

        verify(kafkaTemplate, times(1)).send(recordCaptor.capture());
        assertThat(result).isNotDone();
        first.complete(acknowledged(recordCaptor.getValue()));
        verify(kafkaTemplate, times(2)).send(recordCaptor.capture());
        assertThat(recordCaptor.getValue().value()).isSameAs(events.get(1));
        second.complete(acknowledged(recordCaptor.getValue()));
        assertThat(result.join()).extracting(PublishReceipt::eventId)
                .containsExactly(events.get(0).eventId(), events.get(1).eventId());
    }

    @Test
    void batchStopsAfterFailureWithoutSendingRemainingEvents() {
        var events = List.of(event(), event(), event());
        var firstRecord = new ProducerRecord<>(TOPIC, events.getFirst().orderId().toString(), events.getFirst());
        when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, OrderEvent>>any()))
                .thenReturn(CompletableFuture.completedFuture(acknowledged(firstRecord)))
                .thenReturn(CompletableFuture.failedFuture(new KafkaException("Broker unavailable")));

        var exception = assertThrows(CompletionException.class, () -> producer.publishBatch(events).join());

        verify(kafkaTemplate, times(2)).send(recordCaptor.capture());
        assertThat(recordCaptor.getAllValues()).extracting(ProducerRecord::value)
                .containsExactly(events.get(0), events.get(1));
        assertThat(exception.getCause()).isInstanceOf(EventPublishException.class);
        assertThat(((EventPublishException) exception.getCause()).eventId()).isEqualTo(events.get(1).eventId());
    }

    private OrderEventProducer producerWithTimeout(Duration timeout) {
        return new OrderEventProducer(kafkaTemplate, new OrderEventProperties(TOPIC, 3, 1, timeout),
                Mappers.getMapper(PublishReceiptMapper.class));
    }

    private static OrderEvent event() {
        return new OrderEvent(UUID.randomUUID(), OrderEventType.ORDER_CANCELLED, 1,
                Instant.parse("2026-10-05T12:00:00Z"), UUID.randomUUID(), new OrderEventPayload.Cancelled("Demo"));
    }

    private static SendResult<String, OrderEvent> acknowledged(ProducerRecord<String, OrderEvent> record) {
        var metadata = new RecordMetadata(new TopicPartition(record.topic(), 2), 42, 0, 0, 0, 0);
        return new SendResult<>(record, metadata);
    }

    private static String header(ProducerRecord<String, OrderEvent> record, String name) {
        return new String(record.headers().lastHeader(name).value(), StandardCharsets.UTF_8);
    }
}
