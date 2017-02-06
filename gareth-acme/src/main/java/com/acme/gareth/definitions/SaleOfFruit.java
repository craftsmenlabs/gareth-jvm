package com.acme.gareth.definitions;

import org.craftsmenlabs.ExperimentDefinition;
import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleOfFruit implements ExperimentDefinition {

    @Autowired
    private MockDB mockDB;

    @Assume(glueLine = "^sale of fruit has risen by (\\d+?) per cent$")
    public boolean hasRisenByPercent(final int percentage) {
        return percentage > 80;
    }

}
