package org.craftsmenlabs.gareth.time

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


object DateFormatUtils {

    private val DATE_PATTERN = DateTimeFormatter.ofPattern("ddMMyyyy", Locale.ENGLISH)
    private val DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)


    fun parseDateStringToMidnight(ddMMyyyy: String): LocalDateTime {
        val date = LocalDate.parse(ddMMyyyy, DATE_PATTERN)
        return date.atStartOfDay()
    }

    fun parseDateTimeString(ddMMyyyy_hhmm: String): LocalDateTime {
        return LocalDateTime.parse(ddMMyyyy_hhmm, DATE_TIME_PATTERN)
    }

    fun formatToDateString(date: LocalDate): String = date.format(DATE_PATTERN)

}