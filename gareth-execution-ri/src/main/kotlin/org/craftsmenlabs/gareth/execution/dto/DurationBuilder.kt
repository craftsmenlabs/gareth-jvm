package org.craftsmenlabs.gareth.execution.dto

import java.time.Duration

object DurationBuilder {

    fun createForMinutes(duration: Duration) = org.craftsmenlabs.gareth.api.model.Duration("MINUTES", duration.toMinutes())
}