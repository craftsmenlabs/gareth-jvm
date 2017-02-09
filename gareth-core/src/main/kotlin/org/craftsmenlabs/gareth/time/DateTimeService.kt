package org.craftsmenlabs.gareth.time

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Component
@Profile("!mock")
class DateTimeService : TimeService {

    override fun midnight(): LocalDate {
        return LocalDate.now()
    }

    override fun now(): LocalDateTime {
        return LocalDateTime.now()
    }

    override fun toDate(dateTime: LocalDateTime): Date {
        val instant = dateTime.toInstant(ZoneOffset.UTC)
        return Date.from(instant)
    }

    override fun fromDate(dateTime: Date): LocalDateTime {
        val instant = Instant.ofEpochMilli(dateTime.getTime())
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    }

}