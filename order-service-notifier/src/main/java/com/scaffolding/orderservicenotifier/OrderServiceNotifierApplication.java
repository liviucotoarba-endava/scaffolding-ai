package com.scaffolding.orderservicenotifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OrderServiceNotifierApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceNotifierApplication.class, args);
    }
}
