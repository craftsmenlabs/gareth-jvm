package org.craftsmenlabs.gareth2.time

import org.springframework.stereotype.Service

import java.time.LocalDateTime

@Service
class DateTimeService {
    fun now(): LocalDateTime {
        return LocalDateTime.now()
    }
}