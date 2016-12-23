package org.craftsmenlabs.gareth2.time

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DateTimeService {
    fun now(): LocalDateTime {
        return LocalDateTime.now()
    }
}