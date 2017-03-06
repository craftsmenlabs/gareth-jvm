package org.craftsmenlabs.gareth.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.Captors
import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
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

class ExecuteBaselineMonitorTest {

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

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var glueLineExecutor: GlueLineExecutor

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: ExecuteBaselineMonitor

    @Injectable
    lateinit var experimentRunEnvironment: ExperimentRunEnvironment

    @Mocked
    lateinit var schedulers: Schedulers;

    @Before
    fun setUp() {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor = ExecuteBaselineMonitor(experimentProvider, dateTimeService, experimentStorage, glueLineExecutor)
    }

    @Test
    fun shouldOnlyOperateOnStartedExperiments() {
        val glueLines = Gluelines("baseline", "assumption", "success", "failure", "time")
        val timingFinalisationExecuted = ExperimentTiming(
                localDateTime1,
                localDateTime2,
                localDateTime3,
                localDateTime4)
        val timingCompleted = ExperimentTiming(
                localDateTime5,
                localDateTime6,
                localDateTime7,
                localDateTime8,
                localDateTime9
        )

        val results = ExperimentResults()
        val waitingForbaseline = Experiment(id = 0, name = "id", glueLines = glueLines, timing = timingFinalisationExecuted, results = results)
        val baselineExecuted = waitingForbaseline.copy(timing = timingCompleted)
        val experiments = listOf(waitingForbaseline, baselineExecuted)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                glueLineExecutor.executeBaseline(withAny(waitingForbaseline))
                result = ExecutionResult(experimentRunEnvironment, ExecutionStatus.PENDING)

                dateTimeService.now()
                result = localDateTime10
            }
        }

        monitor.start();

        val storageCaptor = Captors.experimentStorage_save(experimentStorage)
        val glueLineExecutorCaptor = Captors.glueLineExecutor_executeBaseline(glueLineExecutor)

        assertThat(glueLineExecutorCaptor).hasSize(1)
        assertThat(glueLineExecutorCaptor[0].id).isEqualTo(waitingForbaseline.id)
        assertThat(glueLineExecutorCaptor[0].timing.baselineExecuted).isNull()

        assertThat(storageCaptor).hasSize(1)
        assertThat(storageCaptor[0].id).isEqualTo(waitingForbaseline.id)

        assertThat(storageCaptor[0]).isEqualTo(
                waitingForbaseline.copy(
                        timing = waitingForbaseline.timing.copy(baselineExecuted = localDateTime10),
                        environment = experimentRunEnvironment))
    }
}
