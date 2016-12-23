package org.craftsmenlabs.gareth2.time

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.model.Experiment
import org.junit.Test
import java.time.Duration

class DurationCalculatorTest {

    private val TIME_TEXT = "TIME_TEXT"

    @Injectable
    lateinit var durationExpressionParser: DurationExpressionParser

    @Injectable
    lateinit var executor: GlueLineExecutor

    @Tested
    lateinit var durationCalculator: DurationCalculator

    @Test
    fun shouldReturnDuration_whenParserReturnsResult(@Injectable experiment: Experiment, @Injectable expected: Duration) {
        object : Expectations() {
            init {
                experiment.details.time
                result = TIME_TEXT

                durationExpressionParser.parse(TIME_TEXT)
                result = expected
            }
        }

        val actual = durationCalculator.getDuration(experiment)

        assertThat(actual).isSameAs(expected);
    }

    @Test
    fun shouldReturnDurationFromExecutor_whenParserReturnsNoResult(@Injectable experiment: Experiment, @Injectable expected: Duration) {
        object : Expectations() {
            init {
                experiment.details.time
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
}