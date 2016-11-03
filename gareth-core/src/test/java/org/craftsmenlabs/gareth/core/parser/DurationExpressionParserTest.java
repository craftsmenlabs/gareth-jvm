package org.craftsmenlabs.gareth.core.parser;

import org.craftsmenlabs.gareth.core.services.DateTimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class DurationExpressionParserTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    LocalDateTime now = LocalDateTime.parse("01-02-2016 12:00", formatter);
    @Mock
    DateTimeService dateTimeService;

    @Before
    public void setup() throws Exception {
        Mockito.when(dateTimeService.now()).thenReturn(now);
        parser = new DurationExpressionParser(dateTimeService);
    }

    private DurationExpressionParser parser;

    @Test
    public void testValidValues() throws Exception {
        assertThat(parser.parseStrict("4 seconds")).isEqualTo(Duration.of(4, ChronoUnit.SECONDS));
        assertThat(parser.parseStrict("1 SECOND")).isEqualTo(Duration.of(1, ChronoUnit.SECONDS));
        assertThat(parser.parseStrict("1 minute")).isEqualTo(Duration.of(1, ChronoUnit.MINUTES));
        assertThat(parser.parseStrict("120 MINUTES")).isEqualTo(Duration.of(120, ChronoUnit.MINUTES));
        assertThat(parser.parseStrict("1 hour")).isEqualTo(Duration.of(1, ChronoUnit.HOURS));
        assertThat(parser.parseStrict("120 HOURS")).isEqualTo(Duration.of(120, ChronoUnit.HOURS));
        assertThat(parser.parseStrict("1 day")).isEqualTo(Duration.of(1, ChronoUnit.DAYS));
        assertThat(parser.parseStrict("120 days")).isEqualTo(Duration.of(120, ChronoUnit.DAYS));
        assertThat(parser.parseStrict("1 week")).isEqualTo(Duration.of(7, ChronoUnit.DAYS));
        assertThat(parser.parseStrict("3 weeks")).isEqualTo(Duration.of(21, ChronoUnit.DAYS));
    }

    @Test
    public void testForLeapYear() {
        Duration oneYear = parser.parseStrict("1 year");
        assertThat(oneYear.get(ChronoUnit.SECONDS) / 86400).isEqualTo(366);
    }

    @Test
    public void testForEndOfMonth() {
        Mockito.when(dateTimeService.now()).thenReturn(LocalDateTime.parse("31-01-2016 12:00", formatter));
        Duration oneMonth = parser.parseStrict("1 month");
        assertThat(oneMonth.get(ChronoUnit.SECONDS) / 86400).isEqualTo(29);

        Mockito.when(dateTimeService.now()).thenReturn(LocalDateTime.parse("31-03-2016 12:00", formatter));
        oneMonth = parser.parseStrict("1 month");
        assertThat(oneMonth.get(ChronoUnit.SECONDS) / 86400).isEqualTo(30);

        Mockito.when(dateTimeService.now()).thenReturn(LocalDateTime.parse("30-04-2016 12:00", formatter));
        oneMonth = parser.parseStrict("1 month");
        assertThat(oneMonth.get(ChronoUnit.SECONDS) / 86400).isEqualTo(30);
    }

    @Test
    public void testInvalidValues() {
        assertThatThrownBy(() -> parser.parseStrict(null)).hasMessage("input string cannot be null");
        assertThatThrownBy(() -> parser.parseStrict("0 day"))
                .hasMessageContaining("value must be between 1 and 99999");
        assertThatThrownBy(() -> parser.parseStrict("day"))
                .hasMessageContaining("input string is not a valid expression");
        assertThatThrownBy(() -> parser.parseStrict("0 days"))
                .hasMessageContaining("value must be between 1 and 99999");
        assertThatThrownBy(() -> parser.parseStrict("-1 week"))
                .hasMessageContaining("input string is not a valid expression");
        assertThatThrownBy(() -> parser.parseStrict("1.5 days"))
                .hasMessageContaining("input string is not a valid expression");
        assertThatThrownBy(() -> parser.parseStrict("1,23 day"))
                .hasMessageContaining("input string is not a valid expression");
        assertThatThrownBy(() -> parser.parseStrict("!@#$%"))
                .hasMessageContaining("input string is not a valid expression");
        assertThatThrownBy(() -> parser.parseStrict("")).hasMessageContaining("input string is not a valid expression");
    }
}