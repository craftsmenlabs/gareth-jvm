package org.craftsmenlabs.gareth2.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Verifications
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth2.ExperimentStorage
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

class IsWaitingForBaselineMonitorTest {

    val localDateTime1 = LocalDateTime.now().minusHours(1)
    val localDateTime2 = LocalDateTime.now().minusHours(2)
    val localDateTime3 = LocalDateTime.now().minusHours(3)
    val localDateTime4 = LocalDateTime.now().minusHours(4)
    val localDateTime5 = LocalDateTime.now().minusHours(5)
    val localDateTime6 = LocalDateTime.now().minusHours(6)
    val localDateTime7 = LocalDateTime.now().minusHours(7)
    val localDateTime8 = LocalDateTime.now().minusHours(8)

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: DateTimeService

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: IsWaitingForBaselineMonitor

    @Before
    fun setUp() {
        monitor = IsWaitingForBaselineMonitor(experimentProvider, dateTimeService, experimentStorage)
    }

    @Test
    fun shouldOnlyOperateOnStartedExperiments() {
        val details = ExperimentDetails("baseline", "assume", "time", "success", "failure", 111, "id")
        val timingStarted = ExperimentTiming(localDateTime1, localDateTime2, localDateTime3)
        val timingWaitingForBaseline = ExperimentTiming(localDateTime4, localDateTime5, localDateTime6, localDateTime7)
        val results = ExperimentResults()
        val experimentStarted = Experiment(details, timingStarted, results)
        val experimentWaitingForBaseline = Experiment(details, timingWaitingForBaseline, results)
        val experiments = listOf(experimentStarted, experimentWaitingForBaseline)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                dateTimeService.now()
                result = localDateTime8
            }
        }

        monitor.start();

        object : Verifications() {
            init {
                experimentStorage.save(experimentStarted)
                times = 1

                experimentStorage.save(experimentWaitingForBaseline)
                times = 0
            }
        }

        assertThat(experimentStarted.timing.waitingForBaseline).isSameAs(localDateTime8)
    }
}
