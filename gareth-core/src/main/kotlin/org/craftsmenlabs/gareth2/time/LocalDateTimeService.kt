package org.craftsmenlabs.gareth2.time

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Profile("!test")
open class LocalDateTimeService : DateTimeService {
    override fun now(): LocalDateTime {
        return LocalDateTime.now()
    }
}
