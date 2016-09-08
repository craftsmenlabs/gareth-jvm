package org.craftsmenlabs.gareth.rest.example.definition;

import org.craftsmenlabs.gareth.api.annotation.*;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class SaleofFruit {

    private static Logger LOGGER = LoggerFactory.getLogger(SaleofFruit.class);

    @Baseline(glueLine = "^sale of (.*?)$")
    public void anotherBaseline(final DefaultStorage storage, final String item) {
        LOGGER.info("Sale of " + item);
    }

    @Assume(glueLine = "^has risen by (\\d+?) per cent$")
    public void hasRisenByPercent(final int percentage) {
        LOGGER.info("rose by percent " + percentage);
        if (percentage < 60) {
            throw new RuntimeException("Expected percentage > 60");
        }
    }

    @Assume(glueLine = "^has risen by at least (\\d+?) items")
    public void hasRisenByItems() {
    }

    @Success(glueLine = "^send (\\d+) bags of (.*?) to (developers|testers)$")
    public void anotherSuccess(final int amount, final String present, String recipient) {
        LOGGER.info("Send {} {} to {}", amount, present, recipient);
    }

    @Time(glueLine = "next Easter")
    public Duration nextEaster() {
        return Duration.of(10L, ChronoUnit.SECONDS);
    }

    @Failure(glueLine = "^Blame the suits$")
    public void anotherFailure() {
        LOGGER.info("You're fired!");
    }
}
