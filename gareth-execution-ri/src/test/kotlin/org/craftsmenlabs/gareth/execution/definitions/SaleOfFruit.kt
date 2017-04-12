package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.validator.*
import org.craftsmenlabs.gareth.validator.model.ExecutionRunContext
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
class SaleOfFruit : ExperimentDefinition {

    @Baseline(glueLine = "^sale of (.*?)$", humanReadable = "sale of fruit", description = "Sale of many things")
    fun getSaleOfItem(context: ExecutionRunContext, item: String) {
        context.storeString("getting value for ", item)
        if (item == "fruit") {
            context.storeLong("fruit", 42)
        }

        if (item == "widgets") {

            context.storeLong("peaches", 50)
        }
    }

    @Assume(glueLine = "^sale of fruit has risen by (\\d+?) per cent$",
            humanReadable = "sale of fruit has risen by whatever percentage",
            description = "Sale of fruit has risen by some percentage")
    fun hasRisenByPercent(percentage: Int): Boolean {
        return percentage > 80
    }

    @Time(glueLine = "next Easter", humanReadable = "next Easter")
    fun nextEaster(): Duration {
        return Duration.of(10L, ChronoUnit.DAYS)
    }

    @Success(glueLine = "^send email to (.*?)$",
            humanReadable = "send email to Moos",
            description = "Send email that the experiment succeeded.")
    fun sendText(runContext: ExecutionRunContext, recipient: String) {
        runContext.storeString("result", "sending success mail to " + recipient)
    }

    @Failure(glueLine = "^send email to (.*?)$",
            humanReadable = "send email to Sam",
            description = "Send email that the experiment failed.")
    fun sendFailureEmail(runContext: ExecutionRunContext, recipient: String) {
        runContext.storeString("result", "sending failure mail to " + recipient)
    }

    @Failure(glueLine = "^send text to (.*?)$",
            humanReadable = "send text to Sam",
            description = "Send text that the experiment failed")
    fun sendFailureText(runContext: ExecutionRunContext, recipient: String) {
        runContext.storeString("result", "sending failure text to " + recipient)
    }

}