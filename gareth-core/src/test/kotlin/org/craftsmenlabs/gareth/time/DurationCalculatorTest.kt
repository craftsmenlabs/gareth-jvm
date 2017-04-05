package org.craftsmenlabs.gareth.time

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class DurationCalculatorTest {

    private val TIME_TEXT = "TIME_TEXT"

    @Injectable
    lateinit var durationExpressionParser: DurationExpressionParser

    @Injectable
    lateinit var executor: GlueLineExecutor

    @Injectable
    lateinit var timeService: DateTimeService

    @Tested
    lateinit var durationCalculator: DurationCalculator

    @Test
    fun shouldReturnDuration_whenParserReturnsResult(@Injectable experiment: ExperimentDTO, @Injectable expected: Duration) {
        object : Expectations() {
            init {
                experiment.glueLines.time
                result = TIME_TEXT

                durationExpressionParser.parse(TIME_TEXT)
                result = expected
            }
        }

        val actual = durationCalculator.getDuration(experiment)

        assertThat(actual).isSameAs(expected);
    }

    @Test
    fun shouldReturnDurationFromExecutor_whenParserReturnsNoResult(@Injectable experiment: ExperimentDTO, @Injectable expected: Duration) {
        object : Expectations() {
            init {
                experiment.glueLines.time
                result = TIME_TEXT

                durationExpressionParser.parse(TIME_TEXT)
                result = null

                executor.getDuration(experiment)
                result = expected
            }
        }

        val actual = durationCalculator.getDuration(experiment)

        assertThat(actual).isSameAs(expected);
    }

    @Test
    fun testcalculateDuration() {
        val now = LocalDateTime.now();
        val inOneHour = now.plusHours(1)

        assertThat(durationCalculator.getDifferenceInSeconds(now, now, Duration.of(15, ChronoUnit.MINUTES))).isEqualTo(900)
        assertThat(durationCalculator.getDifferenceInSeconds(now, inOneHour, Duration.of(2, ChronoUnit.HOURS))).isEqualTo(3 * 3600)

    }
}