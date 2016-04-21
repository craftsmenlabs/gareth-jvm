package org.craftsmenlabs.gareth.rest.example.definition;

import org.craftsmenlabs.gareth.api.annotation.*;
import org.craftsmenlabs.gareth.api.storage.Storage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Created by hylke on 14/08/15.
 */
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
    }

    @Time(glueLine = "Another 1 minute")
    public Duration anotherTime() {
        return Duration.of(1L, ChronoUnit.MINUTES);
    }

    @Time(glueLine = "Another 1 month")
    public Duration anotherTimeOneMonth() {
        return Duration.of(2L, ChronoUnit.MINUTES);
    }

    @Time(glueLine = "Another 1 year")
    public Duration anotherTimeOneYear() {
        return Duration.of(3L, ChronoUnit.MINUTES);
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
