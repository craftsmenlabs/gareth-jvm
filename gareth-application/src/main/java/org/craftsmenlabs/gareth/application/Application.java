package org.craftsmenlabs.gareth.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }
}
