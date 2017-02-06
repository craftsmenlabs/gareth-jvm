package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.ExperimentDefinition
import org.craftsmenlabs.gareth.api.annotation.*
import org.craftsmenlabs.gareth.model.ExecutionRunContext
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
class SaleOfFruit : ExperimentDefinition {

    @Baseline(glueLine = "^sale of (.*?)$")
    fun getSaleOfItem(context: ExecutionRunContext, item: String) {
        context.storeString("getting value for ", item)
        if (item == "fruit") {
            context.storeLong("fruit", 42)
        }

        if (item == "widgets") {

            context.storeLong("peaches", 50)
        }
    }

    @Assume(glueLine = "^sale of fruit has risen by (\\d+?) per cent$")
    fun hasRisenByPercent(percentage: Int): Boolean {
        return percentage > 80
    }

    @Time(glueLine = "next Easter")
    fun nextEaster(): Duration {
        return Duration.of(10L, ChronoUnit.DAYS)
    }

    @Success(glueLine = "^send email to (.*?)$")
    fun sendText(runContext: ExecutionRunContext, recipient: String) {
        runContext.storeString("result", "sending success mail to " + recipient)
    }

    @Failure(glueLine = "^send email to (.*?)$")
    fun sendFailureEmail(runContext: ExecutionRunContext, recipient: String) {
        runContext.storeString("result", "sending failure mail to " + recipient)
    }

    @Failure(glueLine = "^send text to (.*?)$")
    fun sendFailureText(runContext: ExecutionRunContext, recipient: String) {
        runContext.storeString("result", "sending failure text to " + recipient)
    }

}