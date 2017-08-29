package com.acme.gareth.definitions;

import org.craftsmenlabs.gareth.validator.Assume;
import org.craftsmenlabs.gareth.validator.Baseline;
import org.craftsmenlabs.gareth.validator.ExperimentDefinition;
import org.craftsmenlabs.gareth.validator.Failure;
import org.craftsmenlabs.gareth.validator.Success;
import org.craftsmenlabs.gareth.validator.model.RunContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AcmeGluelines implements ExperimentDefinition {

    @Autowired
    private MockDB mockDB;

    private boolean toggleStatus;

    @Baseline(glueLine = "^sale of (apples|bananas|peaches)$",
            humanReadable = "Sale of [apples,bananas,peaches]")
    public void getSaleOfItem(final RunContext context, final String item) {
        context.storeDouble(item, mockDB.getSalesForProductAtBaseline(item));
    }

    @Baseline(glueLine = "^fresh snake oil$",
            humanReadable = "Sale of oil")
    public void saleOfSnakeOil(final RunContext context) {
        throw new IllegalArgumentException("There is no such thing as snake oil!");
    }

    @Assume(glueLine = "^sale of (apples|bananas|peaches) has risen by (\\d+?) per cent$",
            humanReadable = "sale of <apples,bananas,peaches> has risen by <0-100> per cent]",
            description = "sale of apples,bananas,peaches and risen by 0-100 per cent")
    public boolean hasRisenByPercent(final RunContext context, final String product, final int percentageExpected) {
        double salesAtBaseline = context.getDouble(product);
        double salesForProductAtAssume = mockDB.getSalesForProductAtAssume(product);
        double increaseInPercentage = 100 * ((salesForProductAtAssume / salesAtBaseline) - 1);
        context.storeDouble("actual increase", increaseInPercentage);
        return increaseInPercentage >= percentageExpected;
    }

    @Assume(glueLine = "^how about that snake oil$",
            humanReadable = "How about that snake oil?")
    public boolean assumeSnakeOil(final RunContext context) {
        throw new IllegalArgumentException("For the last time: there is no such thing as snake oil!");
    }

    @Assume(glueLine = "toggle success and failure", humanReadable = "toggle success and failure", description = "Testing glueline which toggles between a failed or successful outcome.")
    public boolean toggle() {
        toggleStatus = !toggleStatus;
        return toggleStatus;
    }

    @Success(glueLine = "^send email to (.*?)$", humanReadable = "send email to <person>")
    public void sendEmail(RunContext runContext, String recipient) {
        runContext.storeString("result", "sending success mail to " + recipient);
    }

    @Success(glueLine = "^send text to (.*?)$", humanReadable = "send text to <person>")
    public void sendText(RunContext runContext, String recipient) {
        runContext.storeString("result", "sending success text to " + recipient);
    }

    @Failure(glueLine = "^send email to (.*?)$", humanReadable = "send email to <person>")
    public void sendFailureEmail(RunContext runContext, String recipient) {
        runContext.storeString("result", "sending failure mail to " + recipient);
    }

    @Failure(glueLine = "^send text to (.*?)$", humanReadable = "send text to <person>")
    public void sendFailureText(RunContext runContext, String recipient) {
        runContext.storeString("result", "sending failure text to " + recipient);
    }


}
