package org.craftsmenlabs.gareth2.time

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Component
class DateTimeService {
    fun now(): LocalDateTime {
        return LocalDateTime.now()
    }

    fun toDate(dateTime: LocalDateTime): Date {
        val instant = dateTime.toInstant(ZoneOffset.UTC)
        return Date.from(instant)
    }

    fun fromDate(dateTime: Date): LocalDateTime {
        val instant = Instant.ofEpochMilli(dateTime.getTime())
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    }

}