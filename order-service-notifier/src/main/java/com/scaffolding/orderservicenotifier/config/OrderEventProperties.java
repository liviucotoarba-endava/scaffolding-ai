package com.scaffolding.orderservicenotifier.config;

import java.time.Duration;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.events")
public record OrderEventProperties(
        @NotBlank String topic,
        @Min(1) int partitions,
        @Min(1) @Max(Short.MAX_VALUE) int replicas,
        @NotNull @DurationMin(seconds = 1) Duration publishTimeout) {
}
