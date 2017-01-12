package org.craftsmenlabs.gareth2.util

import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Profile("test")
open class WrappedDateTimeService : DateTimeService {

    lateinit var mock : DateTimeService

    override fun now(): LocalDateTime {
        return mock.now()
    }
}
