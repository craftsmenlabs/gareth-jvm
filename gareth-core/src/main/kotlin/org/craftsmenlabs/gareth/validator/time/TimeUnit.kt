package org.craftsmenlabs.gareth.validator.time

enum class TimeUnit {
    SECOND, SECONDS, MINUTE, MINUTES, HOUR, HOURS, DAY, DAYS, WEEK, WEEKS, MONTH, MONTHS, YEAR, YEARS;

    companion object {
        fun safeParse(txt: String): TimeUnit {
            try {
                return valueOf(txt.trim { it <= ' ' }.toUpperCase())
            } catch (e: Exception) {
                throw IllegalArgumentException("Value for duration must be one of " + values())
            }
        }
    }
}