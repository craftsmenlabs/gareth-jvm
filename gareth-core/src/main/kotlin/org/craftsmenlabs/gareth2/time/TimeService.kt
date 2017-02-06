package org.craftsmenlabs.gareth2.time

import java.time.LocalDateTime
import java.util.*


interface TimeService {
    fun now(): LocalDateTime

    fun toDate(dateTime: LocalDateTime): Date

    fun fromDate(dateTime: Date): LocalDateTime

    fun parse_ddMMYYY(input: String): LocalDateTime
}