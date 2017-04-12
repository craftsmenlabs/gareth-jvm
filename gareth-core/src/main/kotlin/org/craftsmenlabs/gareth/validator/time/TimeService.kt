package org.craftsmenlabs.gareth.validator.time

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


interface TimeService {

    fun midnight(): LocalDate

    fun now(): LocalDateTime

    fun toDate(dateTime: LocalDateTime): Date

    fun fromDate(dateTime: Date): LocalDateTime

    fun getSecondsUntil(inFuture: LocalDateTime): Long
}