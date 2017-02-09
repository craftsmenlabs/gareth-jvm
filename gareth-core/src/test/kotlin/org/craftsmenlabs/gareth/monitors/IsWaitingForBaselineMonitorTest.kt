package org.craftsmenlabs.gareth.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.Captors
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentDetails
import org.craftsmenlabs.gareth.model.ExperimentResults
import org.craftsmenlabs.gareth.model.ExperimentTiming
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.craftsmenlabs.monitorintegration.computationTestOverride
import org.craftsmenlabs.monitorintegration.ioTestOverride
import org.junit.Before
import org.junit.Test
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
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
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: IsWaitingForBaselineMonitor

    @Mocked
    lateinit var schedulers: Schedulers;

    @Before
    fun setUp() {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor = IsWaitingForBaselineMonitor(experimentProvider, dateTimeService, experimentStorage)
    }

    @Test
    fun shouldOnlyOperateOnStartedExperiments() {
        val details = ExperimentDetails("name", "baseline", "assume", "time", "success", "failure", 111)
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

        val storageCaptor = Captors.experimentStorage_save(experimentStorage)

        assertThat(storageCaptor).hasSize(1);

        assertThat(storageCaptor[0]).isEqualTo(
                experimentStarted.copy(
                        timing = experimentStarted.timing.copy(waitingForBaseline = localDateTime8)))
    }
}
