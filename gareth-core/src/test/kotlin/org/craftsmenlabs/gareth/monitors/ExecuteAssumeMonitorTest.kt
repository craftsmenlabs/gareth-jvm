package org.craftsmenlabs.gareth.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.Captors
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.craftsmenlabs.monitorintegration.computationTestOverride
import org.craftsmenlabs.monitorintegration.ioTestOverride
import org.junit.Before
import org.junit.Test
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
import java.time.LocalDateTime

class ExecuteAssumeMonitorTest {

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

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var glueLineExecutor: GlueLineExecutor

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    @Injectable
    lateinit var experimentRunEnvironment: ExperimentRunEnvironment

    lateinit var monitor: ExecuteAssumeMonitor

    @Mocked
    lateinit var schedulers: Schedulers;

    @Before
    fun setUp() {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor = ExecuteAssumeMonitor(experimentProvider, dateTimeService, experimentStorage, glueLineExecutor)
    }

    @Test
    fun shouldOnlyOperateOnStartedExperiments() {
        val details = ExperimentDetails("id", "baseline", "assumption", "time", "success", "failure", 111)
        val timingFinalisationExecuted = ExperimentTiming(
                localDateTime1,
                localDateTime2,
                localDateTime3,
                localDateTime4,
                localDateTime5,
                localDateTime6)
        val timingCompleted = ExperimentTiming(
                localDateTime7,
                localDateTime8,
                localDateTime9,
                localDateTime10,
                localDateTime11,
                localDateTime12,
                localDateTime13
        )

        val results = ExperimentResults()
        val waitingForAssume = Experiment(details, timingFinalisationExecuted, results)
        val assumeExecuted = Experiment(details, timingCompleted, results)
        val experiments = listOf(waitingForAssume, assumeExecuted)
        val status = ExecutionStatus.SUCCESS

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                glueLineExecutor.executeAssume(withAny(waitingForAssume))
                result = ExecutionResult(experimentRunEnvironment, status)

                dateTimeService.now()
                result = localDateTime14
            }
        }

        monitor.start();

        val storageCaptor = Captors.experimentStorage_save(experimentStorage)
        val glueLineExecutorCaptor = Captors.glueLineExecutor_executeAssume(glueLineExecutor)

        assertThat(glueLineExecutorCaptor).hasSize(1)
        assertThat(glueLineExecutorCaptor[0].id).isEqualTo(waitingForAssume.id)
        assertThat(glueLineExecutorCaptor[0].timing.assumeExecuted).isNull()

        assertThat(storageCaptor).hasSize(1)
        assertThat(storageCaptor[0].id).isEqualTo(waitingForAssume.id)

        assertThat(storageCaptor[0]).isEqualTo(
                waitingForAssume.copy(
                        timing = waitingForAssume.timing.copy(assumeExecuted = localDateTime14),
                        results = waitingForAssume.results.copy(status = status),
                        environment = experimentRunEnvironment))
    }
}
