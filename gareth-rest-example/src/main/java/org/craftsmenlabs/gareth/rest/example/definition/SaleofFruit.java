package org.craftsmenlabs.gareth.rest.example.definition;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.api.storage.Storage;

public class SaleofFruit {

    @Baseline(glueLine = "^sale of (.*?)$")
    public void anotherBaseline(final Storage storage) {
    }

    @Assume(glueLine = "^has risen by (\\d+?) per cent$")
    public void hasRisenByPercent() {
    }

    @Assume(glueLine = "^has risen by at least (\\d+?) items")
    public void hasRisenByItems() {
    }

    @Success(glueLine = "^send (.*?) to developers$")
    public void anotherSuccess() {

    }

    @Failure(glueLine = "^Blame the suits$")
    public void anotherFailure() {

    }
}
