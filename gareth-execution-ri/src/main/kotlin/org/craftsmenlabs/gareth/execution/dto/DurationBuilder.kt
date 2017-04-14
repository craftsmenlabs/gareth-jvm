package org.craftsmenlabs.gareth.execution.dto

import java.time.Duration

object DurationBuilder {

    fun createForMinutes(duration: Duration) = org.craftsmenlabs.gareth.validator.model.Duration("MINUTES", duration.toMinutes())
}