package org.craftsmenlabs.gareth.examples.definition;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.core.expect.Expect;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class SampleDefinition {

    @Baseline(glueLine = "sale of (.*?)")
    public void sampleBaseline(Storage storage, String product) {
        storage.store(product, 100f);
        System.out.println("Getting sale of " + product);
    }

    @Assume(glueLine = "sale of (.*?) has gone up by (\\d{1,3})%")
    public void sampleAssume(Storage storage, String product, int percentage) {
        Float salesLastMonth = storage.get(product, Float.class).get();
        float salesThisMonth = 105f;
        float salesIncrease = salesThisMonth / salesLastMonth;
        float ratioExpected = 1 + ((float) percentage / 100f);
        if (salesIncrease < ratioExpected)
            Expect.fail("sales percentage is " + salesIncrease);
        System.out.println("Sale of " + product + " has gone up by percentage " + salesIncrease);
    }


    //Duration glueline is optional if experiment follows patters XX SECONDS/MINUTES/HOURS...
    @Time(glueLine = "1 minute")
    public Duration sampleTime() {
        return Duration.of(2L, ChronoUnit.SECONDS);
    }

    @Success(glueLine = "order (\\d{1,3}) (.*?) from (.*?)")
    public void sampleSuccess(int amount, String treat, String supplier) {
        System.out.println("Stuff yourself with " + treat + " from " + supplier);
    }

    @Failure(glueLine = "fire (\\d+?) (.*?) at random")
    public void sampleFailure(String nmb, String victim) {
        System.out.println("Firing " + nmb + " " + victim);
    }
}
