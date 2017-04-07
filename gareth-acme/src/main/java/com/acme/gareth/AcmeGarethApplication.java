package com.acme.gareth;

import org.craftsmenlabs.gareth.execution.GarethExecutionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = "com.acme.gareth")
@Import(GarethExecutionConfig.class)
public class AcmeGarethApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcmeGarethApplication.class, args);
    }
}
