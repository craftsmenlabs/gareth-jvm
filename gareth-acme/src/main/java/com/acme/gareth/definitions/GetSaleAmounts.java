package com.acme.gareth.definitions;

import org.craftsmenlabs.ExperimentDefinition;
import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.model.ExecutionRunContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetSaleAmounts implements ExperimentDefinition {

    @Autowired
    private MockDB mockDB;

    @Baseline(glueLine = "^sale of (apples|bananas|peaches)$")
    public void getSaleOfItem(final ExecutionRunContext context, final String item) {
        context.storeDouble(item, mockDB.getSalesForProductAtBaseline(item));
    }

    @Assume(glueLine = "^sale of (apples|bananas|peaches) has risen by (\\d+?) per cent$")
    public boolean hasRisenByPercent(final ExecutionRunContext context, final String product, final int percentageExpected) {
        double salesAtBaseline = context.getDouble(product);
        double salesForProductAtAssume = mockDB.getSalesForProductAtAssume(product);
        double increaseInPercentage = 100 * ((salesForProductAtAssume / salesAtBaseline) - 1);
        context.storeDouble("actual increase", increaseInPercentage);
        return increaseInPercentage >= percentageExpected;
    }

    @Success(glueLine = "^send email to (.*?)$")
    public void sendEmail(ExecutionRunContext runContext, String recipient) {
        runContext.storeString("result", "sending success mail to " + recipient);
    }

    @Success(glueLine = "^send text to (.*?)$")
    public void sendText(ExecutionRunContext runContext, String recipient) {
        runContext.storeString("result", "sending success text to " + recipient);
    }

    @Failure(glueLine = "^send email to (.*?)$")
    public void sendFailureEmail(ExecutionRunContext runContext, String recipient) {
        runContext.storeString("result", "sending failure mail to " + recipient);
    }

    @Failure(glueLine = "^send text to (.*?)$")
    public void sendFailureText(ExecutionRunContext runContext, String recipient) {
        runContext.storeString("result", "sending failure text to " + recipient);
    }


}
