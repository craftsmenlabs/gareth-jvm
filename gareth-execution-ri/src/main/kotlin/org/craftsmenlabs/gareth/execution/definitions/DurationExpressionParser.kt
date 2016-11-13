package org.craftsmenlabs.gareth.execution.definitions

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.regex.Pattern


class DurationExpressionParser {

    private val PATTERN = Pattern.compile("(\\d{1,5}) ?([a-zA-Z]{3,7})")

    fun parse(text: String): Optional<Duration> {
        try {
            return Optional.of(parseStrict(text))
        } catch (e: IllegalArgumentException) {
            return Optional.empty<Duration>()
        }

    }

    fun parseStrict(text: String?): Duration {
        if (text == null)
            throw IllegalArgumentException("input string cannot be null")
        val matcher = PATTERN.matcher(text)
        if (matcher == null || !matcher.matches())
            throw IllegalArgumentException("input string is not a valid expression: " + text)
        return parse(matcher.group(1), matcher.group(2))
    }

    private fun parse(amount: String, unit: String): Duration {
        val i = Integer.parseInt(amount)
        if (i < 1 || i > 99999)
            throw IllegalArgumentException("value must be between 1 and 99999")
        val unit: TimeUnit = TimeUnit.safeParse(unit)
        when (unit) {
            TimeUnit.SECOND, TimeUnit.SECONDS -> return getFixedDuration(ChronoUnit.SECONDS, i)
            TimeUnit.MINUTE, TimeUnit.MINUTES -> return getFixedDuration(ChronoUnit.MINUTES, i)
            TimeUnit.HOUR, TimeUnit.HOURS -> return getFixedDuration(ChronoUnit.HOURS, i)
            TimeUnit.DAY, TimeUnit.DAYS -> return getFixedDuration(ChronoUnit.DAYS, i)
            TimeUnit.WEEK, TimeUnit.WEEKS -> return getFlexibleDuration(ChronoUnit.WEEKS, i)
            TimeUnit.MONTH, TimeUnit.MONTHS -> return getFlexibleDuration(ChronoUnit.MONTHS, i)
            TimeUnit.YEAR, TimeUnit.YEARS -> return getFlexibleDuration(ChronoUnit.YEARS, i)
            else -> throw IllegalArgumentException("Value for duration must be one of ")
        }
    }

    private fun getFixedDuration(unit: ChronoUnit, amount: Int): Duration {
        return Duration.of(amount.toLong(), unit)
    }

    internal fun getFlexibleDuration(unit: TemporalUnit, amount: Int): Duration {
        val now = LocalDateTime.now()
        val later = now.plus(amount.toLong(), unit)
        val millisBetween = Duration.between(now, later).toMillis()
        return Duration.ofMillis(millisBetween)
    }


    enum class TimeUnit {
        SECOND, SECONDS, MINUTE, MINUTES, HOUR, HOURS, DAY, DAYS, WEEK, WEEKS, MONTH, MONTHS, YEAR, YEARS;

        companion object {
            fun safeParse(txt: String): TimeUnit {
                try {
                    return TimeUnit.valueOf(txt.trim { it <= ' ' }.toUpperCase())
                } catch (e: Exception) {
                    throw IllegalArgumentException("Value for duration must be one of " + TimeUnit.values())
                }

            }

        }
    }
}