package org.craftsmenlabs.gareth.core.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommonDurationExpressionParserTest {

    CommonDurationExpressionParser parser = new CommonDurationExpressionParser();

    @Test
    public void testValidValues() {
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
        assertThat(parser.parseStrict("1 year")).isEqualTo(Duration.of(365, ChronoUnit.DAYS));
        assertThat(parser.parseStrict("5 years")).isEqualTo(Duration.of(5 * 365, ChronoUnit.DAYS));
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