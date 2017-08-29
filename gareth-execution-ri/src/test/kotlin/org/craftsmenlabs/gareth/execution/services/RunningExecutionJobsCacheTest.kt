package org.craftsmenlabs.gareth.execution.services

import mockit.Deencapsulation
import mockit.Expectations
import mockit.Injectable
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class RunningExecutionJobsCacheTest {

    val ID = "42"
    @Injectable
    lateinit var timeService: TimeService
    val now = LocalDateTime.now()
    lateinit var cache: RunningExecutionJobsCache

    @Before
    fun setup() {
        object : Expectations() {
            init {
                timeService.now()
                result = now
                minTimes = 0
            }
        }
        cache = RunningExecutionJobsCache(timeService)
        Deencapsulation.setField(cache,"cacheExpirySeconds",1)
    }

    @Test
    fun testRegisterExperiment() {
        cache.registerExperiment(ID)
        assertThat(cache.isInProgress(ID))
        assertThat(cache.canSkip(ID)).isTrue()
        assertThat(cache.isInProgress("43")).isFalse()
        assertThat(cache.isWithinTimeframe(ID)).isTrue()
    }

    @Test
    fun testCannotRegisterTwice() {
        cache.registerExperiment(ID)
        assertThatThrownBy { cache.registerExperiment(ID) }.hasMessage("Experiment 42 still in progress")
    }


    @Test
    fun testPopExperiment() {
        cache.registerExperiment(ID)
        assertThat(cache.isInProgress(ID)).isTrue()
        cache.popExperiment(ID)
        assertThat(cache.isInProgress(ID)).isFalse()
    }

    @Test
    fun testExpiry() {
        cache.registerExperiment(ID)
        object : Expectations() {
            init {
                timeService.now()
                result = now.plusSeconds(1)
            }
        }
        assertThat(cache.isWithinTimeframe(ID)).isFalse()
        assertThat(cache.isInProgress(ID)).isTrue()
        assertThat(cache.canSkip(ID)).isFalse()
    }


}