package org.craftsmenlabs.gareth.time

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.regex.Pattern

/**
 * Utility to parse common time expression to a Duration, e.g. 1 minute, 5 hours, 3 days
 */
@Component
open class DurationExpressionParser constructor(@Autowired val dateTimeService: TimeService) {

    private val PATTERN = Pattern.compile("(\\d{1,5}) ?([a-zA-Z]{3,7})")

    fun parse(text: String): Duration? {
        val matcher = PATTERN.matcher(text)
        if (matcher == null || !matcher.matches()) {
            return null
        }
        return parse(matcher.group(1), matcher.group(2))
    }

    private fun parse(amount: String, unit: String): Duration? {
        val value = amount.toInt()
        if (value < 1 || value > 99999) {
            return null
        }

        when (TimeUnit.safeParse(unit)) {
            DurationExpressionParser.TimeUnit.SECOND, DurationExpressionParser.TimeUnit.SECONDS -> return getFixedDuration(ChronoUnit.SECONDS, value)
            DurationExpressionParser.TimeUnit.MINUTE, DurationExpressionParser.TimeUnit.MINUTES -> return getFixedDuration(ChronoUnit.MINUTES, value)
            DurationExpressionParser.TimeUnit.HOUR, DurationExpressionParser.TimeUnit.HOURS -> return getFixedDuration(ChronoUnit.HOURS, value)
            DurationExpressionParser.TimeUnit.DAY, DurationExpressionParser.TimeUnit.DAYS -> return getFixedDuration(ChronoUnit.DAYS, value)
            DurationExpressionParser.TimeUnit.WEEK, DurationExpressionParser.TimeUnit.WEEKS -> return getFlexibleDuration(ChronoUnit.WEEKS, value)
            DurationExpressionParser.TimeUnit.MONTH, DurationExpressionParser.TimeUnit.MONTHS -> return getFlexibleDuration(ChronoUnit.MONTHS, value)
            DurationExpressionParser.TimeUnit.YEAR, DurationExpressionParser.TimeUnit.YEARS -> return getFlexibleDuration(ChronoUnit.YEARS, value)
            else -> return null
        }
    }

    private fun getFixedDuration(unit: ChronoUnit, amount: Int): Duration {
        return Duration.of(amount.toLong(), unit)
    }

    private fun getFlexibleDuration(unit: TemporalUnit, amount: Int): Duration {
        val now = dateTimeService.now()


        val later = now.plus(amount.toLong(), unit)
        val millisBetween = Duration.between(now, later).toMillis()
        return Duration.ofMillis(millisBetween)
    }


    private enum class TimeUnit {
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
