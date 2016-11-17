package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleOfFruit {

    @Autowired
    private MockDB mockDB;

    @Assume(glueLine = "^sale of fruit has risen by (\\d+?) per cent$")
    public boolean hasRisenByPercent(final int percentage) {
        return percentage > 80;
    }

}
