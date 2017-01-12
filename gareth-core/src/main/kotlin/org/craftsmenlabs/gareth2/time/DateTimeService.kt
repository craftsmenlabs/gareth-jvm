package org.craftsmenlabs.gareth2.time

import java.time.LocalDateTime

interface DateTimeService {
    fun now(): LocalDateTime
}
