package com.scaffolding.orderservicenotifier.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.scaffolding.orderservicenotifier.demo.DemoEventFactory;
import com.scaffolding.orderservicenotifier.event.OrderEvent;
import com.scaffolding.orderservicenotifier.mappers.OrderEventMapper;
import com.scaffolding.orderservicenotifier.producer.OrderEventProducer;
import com.scaffolding.orderservicenotifier.producer.PublishReceipt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-events")
@RequiredArgsConstructor
public class OrderEventController {

    private final OrderEventProducer producer;
    private final DemoEventFactory demoEventFactory;
    private final OrderEventMapper eventMapper;

    @PostMapping("/created")
    public CompletableFuture<PublishReceipt> created(
            @Valid @RequestBody OrderEventRequests.Created request,
            @RequestParam(defaultValue = "ASYNC") PublishMode mode) {
        return publish(eventMapper.toEvent(request), mode);
    }

    @PostMapping("/paid")
    public CompletableFuture<PublishReceipt> paid(
            @Valid @RequestBody OrderEventRequests.Paid request,
            @RequestParam(defaultValue = "ASYNC") PublishMode mode) {
        return publish(eventMapper.toEvent(request), mode);
    }

    @PostMapping("/cancelled")
    public CompletableFuture<PublishReceipt> cancelled(
            @Valid @RequestBody OrderEventRequests.Cancelled request,
            @RequestParam(defaultValue = "ASYNC") PublishMode mode) {
        return publish(eventMapper.toEvent(request), mode);
    }

    @PostMapping("/demo-batch")
    public CompletableFuture<List<PublishReceipt>> demoBatch() {
        return producer.publishBatch(demoEventFactory.createBatch());
    }

    private CompletableFuture<PublishReceipt> publish(OrderEvent event, PublishMode mode) {
        return switch (mode) {
            case ASYNC -> producer.publish(event);
            case BLOCKING -> CompletableFuture.completedFuture(producer.publishBlocking(event));
        };
    }
}
