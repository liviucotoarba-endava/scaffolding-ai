package com.scaffolding.userserviceapi.controllers;

import com.scaffolding.userserviceapi.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleUserNotFoundReturns404WithExceptionMessage() {
        ProblemDetail problem = handler.handleUserNotFound(new UserNotFoundException(99L));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getDetail()).isEqualTo("User with id 99 was not found");
    }
}
