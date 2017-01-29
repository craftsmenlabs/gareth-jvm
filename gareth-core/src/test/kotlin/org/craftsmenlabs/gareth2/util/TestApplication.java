package org.craftsmenlabs.gareth2.util;

import org.craftsmenlabs.gareth2.config.CoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CoreConfiguration.class})
public class TestApplication {
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(TestApplication.class, args);
    }
}
