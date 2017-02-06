package org.craftsmenlabs.monitorintegration

import org.craftsmenlabs.gareth2.time.TimeService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
@Profile("mock")
open class WrappedDateTimeService : TimeService {

    override fun parse_ddMMYYY(input: String): LocalDateTime {
        return mock.parse_ddMMYYY(input)
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
