package org.craftsmenlabs.gareth2.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Verifications
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GluelineLookup
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentDetails
import org.craftsmenlabs.gareth2.model.ExperimentResults
import org.craftsmenlabs.gareth2.model.ExperimentTiming
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.junit.Before
import org.junit.Test
import rx.lang.kotlin.toObservable
import java.time.LocalDateTime

class IsCompletedMonitorTest {

    val localDateTime1 = LocalDateTime.now().minusHours(1)
    val localDateTime2 = LocalDateTime.now().minusHours(2)
    val localDateTime3 = LocalDateTime.now().minusHours(3)
    val localDateTime4 = LocalDateTime.now().minusHours(4)
    val localDateTime5 = LocalDateTime.now().minusHours(5)
    val localDateTime6 = LocalDateTime.now().minusHours(6)
    val localDateTime7 = LocalDateTime.now().minusHours(7)
    val localDateTime8 = LocalDateTime.now().minusHours(8)
    val localDateTime9 = LocalDateTime.now().minusHours(9)
    val localDateTime10 = LocalDateTime.now().minusHours(10)
    val localDateTime11 = LocalDateTime.now().minusHours(11)
    val localDateTime12 = LocalDateTime.now().minusHours(12)
    val localDateTime13 = LocalDateTime.now().minusHours(13)
    val localDateTime14 = LocalDateTime.now().minusHours(14)
    val localDateTime15 = LocalDateTime.now().minusHours(15)
    val localDateTime16 = LocalDateTime.now().minusHours(16)
    val localDateTime17 = LocalDateTime.now().minusHours(17)
    val localDateTime18 = LocalDateTime.now().minusHours(18)

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: DateTimeService

    @Injectable
    lateinit var gluelineLookup: GluelineLookup

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: IsCompletedMonitor

    @Before
    fun setUp() {
        monitor = IsCompletedMonitor(experimentProvider, dateTimeService, experimentStorage)
    }

    @Test
    fun shouldOnlyOperateOnStartedExperiments() {
        val details = ExperimentDetails("name", "baseline", "assume", "time", "success", "failure", 111)
        val timingFinalisationExecuted = ExperimentTiming(
                localDateTime1,
                localDateTime2,
                localDateTime3,
                localDateTime4,
                localDateTime5,
                localDateTime6,
                localDateTime7,
                localDateTime8,
                localDateTime9)
        val timingCompleted = ExperimentTiming(
                localDateTime9,
                localDateTime10,
                localDateTime11,
                localDateTime12,
                localDateTime13,
                localDateTime14,
                localDateTime15,
                localDateTime16,
                localDateTime17,
                localDateTime18
        )
        val results = ExperimentResults()
        val experimentAssumeExecuted = Experiment(details, timingFinalisationExecuted, results, "42")
        val experimentWaitingForFinalisation = Experiment(details, timingCompleted, results, "42")
        val experiments = listOf(experimentAssumeExecuted, experimentWaitingForFinalisation)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                dateTimeService.now()
                result = localDateTime18
            }
        }

        monitor.start();

        object : Verifications() {
            init {
                experimentStorage.save(experimentAssumeExecuted)
                times = 1

                experimentStorage.save(experimentWaitingForFinalisation)
                times = 0
            }
        }

        assertThat(experimentAssumeExecuted.timing.completed).isSameAs(localDateTime18)
    }
}
