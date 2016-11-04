package org.craftsmenlabs.gareth.execution.spi;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.springframework.stereotype.Service;
@Service
public class SaleOfWidgets {

    @Assume(glueLine = "^sale of widgets has risen by (\\d+?) per cent$")
    public void hasRisenByPercent(final int percentage) {
        if (percentage < 20) {
            throw new RuntimeException("Expected percentage > 20");
        }
    }

    @Time(glueLine = "next Easter")
    public Duration nextEaster() {
        return Duration.of(10L, ChronoUnit.SECONDS);
    }

}
