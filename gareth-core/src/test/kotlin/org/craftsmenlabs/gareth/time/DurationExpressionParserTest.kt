package org.craftsmenlabs.gareth.time

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DurationExpressionParserTest {

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    @Injectable
    lateinit var dateTimeService: TimeService

    @Tested
    lateinit var parser: DurationExpressionParser

    @Test
    fun testValidValues() {
        object : Expectations() {
            init {
                dateTimeService.now()
                result = LocalDateTime.parse("01-02-2016 12:00", formatter)
            }
        }

        assertThat(parser.parse("4 seconds")).isEqualTo(Duration.of(4, ChronoUnit.SECONDS))
        assertThat(parser.parse("1 SECOND")).isEqualTo(Duration.of(1, ChronoUnit.SECONDS))
        assertThat(parser.parse("1 minute")).isEqualTo(Duration.of(1, ChronoUnit.MINUTES))
        assertThat(parser.parse("120 MINUTES")).isEqualTo(Duration.of(120, ChronoUnit.MINUTES))
        assertThat(parser.parse("1 hour")).isEqualTo(Duration.of(1, ChronoUnit.HOURS))
        assertThat(parser.parse("120 HOURS")).isEqualTo(Duration.of(120, ChronoUnit.HOURS))
        assertThat(parser.parse("1 day")).isEqualTo(Duration.of(1, ChronoUnit.DAYS))
        assertThat(parser.parse("120 days")).isEqualTo(Duration.of(120, ChronoUnit.DAYS))
        assertThat(parser.parse("1 week")).isEqualTo(Duration.of(7, ChronoUnit.DAYS))
        assertThat(parser.parse("3 weeks")).isEqualTo(Duration.of(21, ChronoUnit.DAYS))
    }

    @Test
    fun testForLeapYear() {
        object : Expectations() {
            init {
                dateTimeService.now()
                result = LocalDateTime.parse("01-02-2016 12:00", formatter)
            }
        }

        val oneYear = parser.parse("1 year")
        assertThat(oneYear!!.get(ChronoUnit.SECONDS) / 86400).isEqualTo(366);
    }

    @Test
    fun testForEndOfMonth() {
        object : Expectations() {
            init {
                dateTimeService.now()
                returns(
                        LocalDateTime.parse("31-01-2016 12:00", formatter),
                        LocalDateTime.parse("31-03-2016 12:00", formatter),
                        LocalDateTime.parse("30-04-2016 12:00", formatter))
            }
        }

        var oneMonth = parser.parse("1 month")
        assertThat(oneMonth!!.get(ChronoUnit.SECONDS) / 86400).isEqualTo(29)

        oneMonth = parser.parse("1 month")
        assertThat(oneMonth!!.get(ChronoUnit.SECONDS) / 86400).isEqualTo(30)

        oneMonth = parser.parse("1 month")
        assertThat(oneMonth!!.get(ChronoUnit.SECONDS) / 86400).isEqualTo(30)
    }

    @Test
    fun testInvalidValues() {
        assertThat(parser.parse("0 day")).isNull()
        assertThat(parser.parse("day")).isNull()
        assertThat(parser.parse("0 days")).isNull()
        assertThat(parser.parse("-1 week")).isNull()
        assertThat(parser.parse("1.5 days")).isNull()
        assertThat(parser.parse("1,23 day")).isNull()
        assertThat(parser.parse("!@#$%")).isNull()
        assertThat(parser.parse("")).isNull()
    }
}