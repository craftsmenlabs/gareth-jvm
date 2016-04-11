package org.craftsmenlabs.gareth.core.parser;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to parse common time expression to a Duration, e.g. 1 minute, 5 hours, 3 days
 */
public class CommonDurationExpressionParser {

    Pattern PATTERN = Pattern.compile("(\\d{1,5}) ?([a-zA-Z]{3,7})");

    private enum TimeUnits {
        SECOND(ChronoUnit.SECONDS, 0),
        SECONDS(ChronoUnit.SECONDS, 0),
        MINUTE(ChronoUnit.MINUTES, 0),
        MINUTES(ChronoUnit.MINUTES, 0),
        HOUR(ChronoUnit.HOURS, 0),
        HOURS(ChronoUnit.HOURS, 0),
        DAY(ChronoUnit.DAYS, 0),
        DAYS(ChronoUnit.DAYS, 0),
        WEEK(null, 7),
        WEEKS(null, 7),
        MONTH(null, 30),
        MONTHS(null, 30),
        YEAR(null, 365),
        YEARS(null, 365);

        ChronoUnit chrono;
        int factor = 0;

        TimeUnits(ChronoUnit chrono, int factor) {
            this.chrono = chrono;
            this.factor = factor;
        }

        static Duration getDuration(String text, int amount) {
            TimeUnits unit = null;
            try {
                unit = TimeUnits.valueOf(text.trim().toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Value for duration must be one of " + TimeUnits.values());
            }
            if (unit.factor == 0)
                return Duration.of(amount, unit.chrono);
            else {
                //A duration can only be constructed from units <= days. Larger units
                // have to be estimated, since a month and year do not have a fixed number of
                return Duration.of(amount * unit.factor, ChronoUnit.DAYS);
            }
        }
    }

    public Optional<Duration> parse(String text) {
        try {
            return Optional.of(parseStrict(text));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Duration parseStrict(String text) {
        if (text == null)
            throw new IllegalArgumentException("input string cannot be null");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher == null || !matcher.matches())
            throw new IllegalArgumentException("input string is not a valid expression: " + text);
        return parse(matcher.group(1), matcher.group(2));
    }

    private Duration parse(String amount, String unit) {
        int i = Integer.parseInt(amount);
        if (i < 1 || i > 99999)
            throw new IllegalArgumentException("value must be between 1 and 99999");
        return TimeUnits.getDuration(unit, i);
    }

}
