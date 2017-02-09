package org.craftsmenlabs.gareth.time

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Component
@Profile("!mock")
class DateTimeService : TimeService {
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

    override fun parse_ddMMYYY(input: String): LocalDateTime {
        val date = LocalDate.parse(input, DateTimeFormatter.ofPattern("ddMMyyyy", Locale.ENGLISH))
        return date.atStartOfDay()
    }

}