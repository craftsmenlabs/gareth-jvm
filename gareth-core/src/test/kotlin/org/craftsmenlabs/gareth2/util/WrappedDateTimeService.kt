package org.craftsmenlabs.gareth2.util

import org.craftsmenlabs.gareth2.time.DateTimeService
import org.craftsmenlabs.gareth2.time.TimeService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
@Profile("test")
open class WrappedDateTimeService : TimeService {

    override fun toDate(dateTime: LocalDateTime): Date {
        return mock.toDate(dateTime)
    }

    override fun fromDate(dateTime: Date): LocalDateTime {
        return mock.fromDate(dateTime)
    }

    lateinit var mock: DateTimeService

    override fun now(): LocalDateTime {
        return mock.now()
    }
}
