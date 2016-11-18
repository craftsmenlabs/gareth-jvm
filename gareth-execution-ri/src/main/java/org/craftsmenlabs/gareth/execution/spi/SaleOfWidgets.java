package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class SaleOfWidgets {

    @Autowired
    private MockDB mockDB;

    @Assume(glueLine = "^sale of widgets has risen by (\\d+?) per cent$")
    public boolean hasRisenByPercent(final int percentage) {
        return percentage > 20;
    }

    @Time(glueLine = "next Easter")
    public Duration nextEaster() {
        return Duration.of(10L, ChronoUnit.DAYS);
    }

}
