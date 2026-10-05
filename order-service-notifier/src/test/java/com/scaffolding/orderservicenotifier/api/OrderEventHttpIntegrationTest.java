package com.scaffolding.orderservicenotifier.api;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import com.scaffolding.orderservicenotifier.event.OrderEvent;
import com.scaffolding.orderservicenotifier.event.OrderEventPayload;
import com.scaffolding.orderservicenotifier.event.OrderEventType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.kafka.admin.auto-create=false")
@AutoConfigureMockMvc
class OrderEventHttpIntegrationTest {

    private static final String CREATED_REQUEST = """
            {
              "orderId": "dc39c3c0-3d36-4eb0-9f4b-fae0331c1a05",
              "userId": "12d7dd87-64de-4633-b9b2-3a858ba1d101",
              "totalAmount": 49.99,
              "currency": "EUR"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Test
    void mapsHttpRequestPublishesEventAndReturnsAcknowledgedReceipt() throws Exception {
        when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, OrderEvent>>any())).thenAnswer(invocation -> {
            ProducerRecord<String, OrderEvent> record = invocation.getArgument(0);
            var metadata = new RecordMetadata(new TopicPartition(record.topic(), 1), 12, 0, 0, 0, 0);
            return CompletableFuture.completedFuture(new SendResult<>(record, metadata));
        });

        var result = mockMvc.perform(post("/api/order-events/created")
                        .contentType(MediaType.APPLICATION_JSON).content(CREATED_REQUEST))
                .andExpect(request().asyncStarted()).andReturn();

        var record = capturePublishedRecord();
        assertThat(record.key()).isEqualTo("dc39c3c0-3d36-4eb0-9f4b-fae0331c1a05");
        assertThat(record.value().eventType()).isEqualTo(OrderEventType.ORDER_CREATED);
        assertThat(record.value().payload()).isInstanceOf(OrderEventPayload.Created.class);
        var payload = (OrderEventPayload.Created) record.value().payload();
        assertThat(payload.totalAmount()).isEqualByComparingTo(new BigDecimal("49.99"));
        assertThat(payload.currency()).isEqualTo("EUR");
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(record.value().eventId().toString()))
                .andExpect(jsonPath("$.topic").value("orders.events.v1"))
                .andExpect(jsonPath("$.partition").value(1))
                .andExpect(jsonPath("$.offset").value(12));
    }

    @Test
    void rejectsInvalidRequestWithoutPublishing() throws Exception {
        mockMvc.perform(post("/api/order-events/created")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void returnsProblemDetailWhenKafkaCannotConfirmDelivery() throws Exception {
        when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, OrderEvent>>any()))
                .thenReturn(CompletableFuture.failedFuture(new KafkaException("Broker unavailable")));

        var result = mockMvc.perform(post("/api/order-events/created")
                        .contentType(MediaType.APPLICATION_JSON).content(CREATED_REQUEST))
                .andExpect(request().asyncStarted()).andReturn();

        var record = capturePublishedRecord();
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.title").value("Order event publication failed"))
                .andExpect(jsonPath("$.eventId").value(record.value().eventId().toString()));
    }

    private ProducerRecord<String, OrderEvent> capturePublishedRecord() {
        ArgumentCaptor<ProducerRecord<String, OrderEvent>> captor = ArgumentCaptor.captor();
        verify(kafkaTemplate).send(captor.capture());
        return captor.getValue();
    }
}
