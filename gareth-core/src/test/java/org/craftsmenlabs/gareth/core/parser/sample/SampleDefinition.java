package org.craftsmenlabs.gareth.core.parser.sample;

import org.craftsmenlabs.gareth.api.annotation.*;

import java.time.Duration;

/**
 * Created by hylke on 13/10/2016.
 */
public class SampleDefinition {

    @Baseline(glueLine = "Baseline glueline")
    public void baselineDefinition() {

    }

    @Assume(glueLine = "Assume glueline")
    public void assumeDefinition() {

    }

    @Success(glueLine = "Success glueline")
    public void successDefinition() {

    }

    @Failure(glueLine = "Failure glueline")
    public void failureDefinition() {

    }

    @Time(glueLine = "Time glueline")
    public Duration timeDefinition() {
        return null;
    }
}
