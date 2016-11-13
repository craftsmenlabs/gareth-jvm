package org.craftsmenlabs.gareth.execution.dto

import java.time.Duration


data class DurationDTO(val unit: String, val amount: Long) {
    companion object {
        fun createForMinutes(duration: Duration) = DurationDTO("MINUTES", duration.toMinutes())
    }
}