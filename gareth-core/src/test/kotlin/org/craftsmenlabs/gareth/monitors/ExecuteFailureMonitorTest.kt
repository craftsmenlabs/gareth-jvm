package org.craftsmenlabs.gareth.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import org.assertj.core.api.Assertions
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

class ExecuteFailureMonitorTest {

    val localDateTime1 = LocalDateTime.now().minusHours(1)      //created
    val localDateTime2 = LocalDateTime.now().minusHours(2)      //ready
    val localDateTime3 = LocalDateTime.now().minusHours(3)      //started
    val localDateTime4 = LocalDateTime.now().minusHours(4)      //waitingForBaseline
    val localDateTime5 = LocalDateTime.now().minusHours(5)      //baselineExecuted
    val localDateTime6 = LocalDateTime.now().minusHours(6)      //waitingForAssume
    val localDateTime7 = LocalDateTime.now().minusHours(7)      //assumeExecuted
    val localDateTime8 = LocalDateTime.now().minusHours(8)      //waitingFinalizing
    val localDateTime9 = LocalDateTime.now().minusHours(9)      //finalizingExecuted
    val localDateTime10 = LocalDateTime.now().minusHours(10)    //
    val localDateTime11 = LocalDateTime.now().minusHours(11)    //
    val localDateTime12 = LocalDateTime.now().minusHours(12)    //
    val localDateTime13 = LocalDateTime.now().minusHours(13)    //
    val localDateTime14 = LocalDateTime.now().minusHours(14)    //
    val localDateTime15 = LocalDateTime.now().minusHours(15)    //
    val localDateTime16 = LocalDateTime.now().minusHours(16)    //
    val localDateTime17 = LocalDateTime.now().minusHours(17)    //
    val localDateTime18 = LocalDateTime.now().minusHours(18)    //

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var glueLineExecutor: GlueLineExecutor

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: ExecuteFailureMonitor

    @Injectable
    lateinit var experimentRunEnvironment: ExperimentRunEnvironment

    @Mocked
    lateinit var schedulers: Schedulers;

    @Before
    fun setUp() {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor = ExecuteFailureMonitor(experimentProvider, dateTimeService, experimentStorage, glueLineExecutor)
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
                localDateTime6,
                localDateTime7,
                localDateTime8)
        val timingCompleted = ExperimentTiming(
                localDateTime9,
                localDateTime10,
                localDateTime11,
                localDateTime12,
                localDateTime13,
                localDateTime14,
                localDateTime15,
                localDateTime16,
                localDateTime17
        )

        val failResults = ExperimentResults(ExecutionStatus.FAILURE)
        val successResults = ExperimentResults(ExecutionStatus.SUCCESS)

        val failedExperimentWaitingForFinalisation = Experiment(details, timingFinalisationExecuted, failResults)
        val succeededExperimentWaitingForFinalisation = Experiment(details, timingFinalisationExecuted, successResults)
        val experimentFinalisationExecuted = Experiment(details, timingCompleted, successResults)
        val experiments = listOf(failedExperimentWaitingForFinalisation, succeededExperimentWaitingForFinalisation, experimentFinalisationExecuted)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                glueLineExecutor.executeFailure(withAny(experimentFinalisationExecuted))
                result = ExecutionResult(experimentRunEnvironment, ExecutionStatus.FAILURE)

                dateTimeService.now()
                result = localDateTime18
            }
        }

        monitor.start();

        val storageCaptor = Captors.experimentStorage_save(experimentStorage)
        val glueLineExecutorCaptor = Captors.glueLineExecutor_executeFailure(glueLineExecutor)

        Assertions.assertThat(glueLineExecutorCaptor).hasSize(1)
        Assertions.assertThat(glueLineExecutorCaptor[0].id).isEqualTo(failedExperimentWaitingForFinalisation.id)
        Assertions.assertThat(glueLineExecutorCaptor[0].timing.finalizingExecuted).isNull()

        Assertions.assertThat(storageCaptor).hasSize(1)
        Assertions.assertThat(storageCaptor[0].id).isEqualTo(failedExperimentWaitingForFinalisation.id)

        Assertions.assertThat(storageCaptor[0]).isEqualTo(
                failedExperimentWaitingForFinalisation.copy(
                        timing = failedExperimentWaitingForFinalisation.timing.copy(finalizingExecuted = localDateTime18),
                        environment = experimentRunEnvironment))
    }
}
