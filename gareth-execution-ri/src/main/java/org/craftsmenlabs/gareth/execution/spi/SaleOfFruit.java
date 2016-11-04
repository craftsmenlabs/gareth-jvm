package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.springframework.stereotype.Service;

@Service
public class SaleOfFruit {

    @Assume(glueLine = "^sale of fruit has risen by (\\d+?) per cent$")
    public void hasRisenByPercent(final int percentage) {
        if (percentage < 60) {
            throw new RuntimeException("Expected percentage > 60");
        }
    }


}
