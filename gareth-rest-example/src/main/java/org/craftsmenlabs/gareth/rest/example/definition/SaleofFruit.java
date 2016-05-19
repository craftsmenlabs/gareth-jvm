package org.craftsmenlabs.gareth.rest.example.definition;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class SaleofFruit {

    private static Logger LOGGER = LoggerFactory.getLogger(SaleofFruit.class);

    @Baseline(glueLine = "^sale of (.*?)$")
    public void anotherBaseline(final Storage storage, final String item) {
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

    @Success(glueLine = "^send (.*?) to developers$")
    public void anotherSuccess(final String present) {
        LOGGER.info("Send to developers " + present);
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
