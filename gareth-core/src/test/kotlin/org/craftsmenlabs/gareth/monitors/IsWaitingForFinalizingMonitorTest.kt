package org.craftsmenlabs.gareth.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.Captors
import org.craftsmenlabs.gareth.GluelineValidator
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentResults
import org.craftsmenlabs.gareth.model.ExperimentTiming
import org.craftsmenlabs.gareth.model.Gluelines
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.craftsmenlabs.monitorintegration.computationTestOverride
import org.craftsmenlabs.monitorintegration.ioTestOverride
import org.junit.Before
import org.junit.Test
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
import java.time.LocalDateTime

class IsWaitingForFinalizingMonitorTest {

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
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var gluelineValidator: GluelineValidator

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: IsWaitingForFinalizingMonitor

    @Mocked
    lateinit var schedulers: Schedulers;

    @Before
    fun setUp() {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor = IsWaitingForFinalizingMonitor(experimentProvider, dateTimeService, experimentStorage)
    }

    @Test
    fun shouldOnlyOperateOnStartedExperiments() {
        val glueLines = Gluelines("baseline", "assumption", "success", "failure", "time")
        val timingAssumeExecuted = ExperimentTiming(
                localDateTime1,
                localDateTime2,
                localDateTime3,
                localDateTime4,
                localDateTime5,
                localDateTime6,
                localDateTime7)
        val timingWaitingForFinalisation = ExperimentTiming(
                localDateTime9,
                localDateTime10,
                localDateTime11,
                localDateTime12,
                localDateTime13,
                localDateTime14,
                localDateTime15,
                localDateTime16
        )
        val results = ExperimentResults()
        val experimentAssumeExecuted = Experiment(id = 0, name = "name", glueLines = glueLines, timing = timingAssumeExecuted, results = results)
        val experimentWaitingForFinalisation = experimentAssumeExecuted.copy(timing = timingWaitingForFinalisation)
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

        val storageCaptor = Captors.experimentStorage_save(experimentStorage)

        assertThat(storageCaptor).hasSize(1);

        assertThat(storageCaptor[0]).isEqualTo(
                experimentAssumeExecuted.copy(
                        timing = experimentAssumeExecuted.timing.copy(waitingFinalizing = localDateTime18)))
    }
}
