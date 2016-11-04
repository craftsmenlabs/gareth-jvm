package org.craftsmenlabs.gareth.execution.parser;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to parse common time expression to a Duration, e.g. 1 minute, 5 hours, 3 days
 */
public class DurationExpressionParser {

    private final Pattern PATTERN = Pattern.compile("(\\d{1,5}) ?([a-zA-Z]{3,7})");

    public Optional<Duration> parse(final String text) {
        try {
            return Optional.of(parseStrict(text));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Duration parseStrict(final String text) {
        if (text == null)
            throw new IllegalArgumentException("input string cannot be null");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher == null || !matcher.matches())
            throw new IllegalArgumentException("input string is not a valid expression: " + text);
        return parse(matcher.group(1), matcher.group(2));
    }

    private Duration parse(final String amount, final String unit) {
        int i = Integer.parseInt(amount);
        if (i < 1 || i > 99999)
            throw new IllegalArgumentException("value must be between 1 and 99999");
        switch (TimeUnit.safeParse(unit)) {
            case SECOND:
            case SECONDS:
                return getFixedDuration(ChronoUnit.SECONDS, i);
            case MINUTE:
            case MINUTES:
                return getFixedDuration(ChronoUnit.MINUTES, i);
            case HOUR:
            case HOURS:
                return getFixedDuration(ChronoUnit.HOURS, i);
            case DAY:
            case DAYS:
                return getFixedDuration(ChronoUnit.DAYS, i);
            case WEEK:
            case WEEKS:
                return getFlexibleDuration(ChronoUnit.WEEKS, i);
            case MONTH:
            case MONTHS:
                return getFlexibleDuration(ChronoUnit.MONTHS, i);
            case YEAR:
            case YEARS:
                return getFlexibleDuration(ChronoUnit.YEARS, i);
            default:
                throw new IllegalArgumentException("Value for duration must be one of ");
        }
    }

    private Duration getFixedDuration(ChronoUnit unit, int amount) {
        return Duration.of(amount, unit);
    }

    Duration getFlexibleDuration(TemporalUnit unit, int amount) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plus(amount, unit);
        long millisBetween = Duration.between(now, later).toMillis();
        return Duration.ofMillis(millisBetween);
    }


    private enum TimeUnit {
        SECOND, SECONDS, MINUTE, MINUTES, HOUR, HOURS, DAY, DAYS, WEEK, WEEKS, MONTH, MONTHS, YEAR, YEARS;

        private static TimeUnit safeParse(String txt) {
            try {
                return TimeUnit.valueOf(txt.trim().toUpperCase());
            } catch (final Exception e) {
                throw new IllegalArgumentException("Value for duration must be one of " + TimeUnit.values());
            }
        }
    }

}
