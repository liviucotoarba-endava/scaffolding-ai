package com.scaffolding.orderservicenotifier.api;

import java.util.concurrent.TimeoutException;

import com.scaffolding.orderservicenotifier.producer.EventPublishException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EventPublishException.class)
    public ProblemDetail handlePublishFailure(EventPublishException exception) {
        var status = exception.getCause() instanceof TimeoutException
                ? HttpStatus.GATEWAY_TIMEOUT : HttpStatus.SERVICE_UNAVAILABLE;
        var problem = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        problem.setTitle("Order event publication failed");
        problem.setProperty("eventId", exception.eventId());
        return problem;
    }
}
