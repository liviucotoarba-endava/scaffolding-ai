package com.scaffolding.orderservicenotifier.api;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class OrderEventRequests {

    private OrderEventRequests() {
    }

    public record Created(
            @NotNull UUID orderId,
            @NotNull UUID userId,
            @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal totalAmount,
            @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency) {
    }

    public record Paid(
            @NotNull UUID orderId,
            @NotBlank @Size(max = 100) String paymentReference,
            @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal paidAmount,
            @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency) {
    }

    public record Cancelled(
            @NotNull UUID orderId,
            @NotBlank @Size(max = 500) String reason) {
    }
}
