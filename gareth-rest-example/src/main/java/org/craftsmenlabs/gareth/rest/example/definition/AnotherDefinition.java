package org.craftsmenlabs.gareth.rest.example.definition;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.craftsmenlabs.gareth.api.storage.Storage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class AnotherDefinition {

    private final Object object;

    public AnotherDefinition(Object object) {
        System.out.println("AnotherDefinition");
        this.object = object;
    }

    @Baseline(glueLine = "Another baseline")
    public void anotherBaseline(final Storage storage) {
        storage.store("time", System.currentTimeMillis());
        storage.store("time2", System.currentTimeMillis());

        System.out.println("Another baseline was executed");
    }

    @Assume(glueLine = "Another assume")
    public void anotherAssume() {
        System.out.println("Another assume was executed");
        throw new RuntimeException("Assumption not met");
    }

    @Time(glueLine = "Another 10 seconds")
    public Duration anotherTimeTenSeconds() {
        return Duration.of(10L, ChronoUnit.SECONDS);
    }

    @Success(glueLine = "Another success")
    public void anotherSuccess() {
        System.out.println("Another success was executed");
    }

    @Failure(glueLine = "Another failure")
    public void anotherFailure() {
        System.out.println("Another failure was executed");
    }
}
