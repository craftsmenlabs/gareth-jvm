package org.craftsmenlabs.monitorintegration

import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
@Profile("mock")
open class WrappedDateTimeService : TimeService {
    override fun getSecondsUntil(inFuture: LocalDateTime): Long {
        return mock.getSecondsUntil(inFuture)
    }

    override fun midnight(): LocalDate {
        return mock.midnight()
    }

    override fun toDate(dateTime: LocalDateTime): Date {
        return mock.toDate(dateTime)
    }

    override fun fromDate(dateTime: Date): LocalDateTime {
        return mock.fromDate(dateTime)
    }

    lateinit var mock: TimeService

    override fun now(): LocalDateTime {
        return mock.now()
    }
}
