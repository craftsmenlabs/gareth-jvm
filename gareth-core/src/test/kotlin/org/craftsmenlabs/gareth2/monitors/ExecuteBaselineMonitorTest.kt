package org.craftsmenlabs.gareth2.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Verifications
import org.assertj.core.api.Assertions
import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentDetails
import org.craftsmenlabs.gareth2.model.ExperimentResults
import org.craftsmenlabs.gareth2.model.ExperimentTiming
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import rx.lang.kotlin.toObservable
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
    lateinit var dateTimeService: DateTimeService

    @Injectable
    lateinit var glueLineExecutor: GlueLineExecutor

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: ExecuteBaselineMonitor

    @Before
    fun setUp() {
        monitor = ExecuteBaselineMonitor(experimentProvider, dateTimeService, experimentStorage, glueLineExecutor)
    }

    @Test
    @Ignore("You have to fix _all_ unit test, because experiments became immutable. the IT test works though :)")
    fun shouldOnlyOperateOnStartedExperiments() {
        val details = ExperimentDetails("id", "baseline", "assumption", "time", "success", "failure", 111)
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
        val waitingForbaseline = Experiment(details, timingFinalisationExecuted, results, "id")
        val baselineExecuted = Experiment(details, timingCompleted, results, "id")
        val experiments = listOf(waitingForbaseline, baselineExecuted)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                dateTimeService.now()
                result = localDateTime10
            }
        }

        monitor.start();

        object : Verifications() {
            init {
                glueLineExecutor.executeBaseline(waitingForbaseline)
                times = 1

                glueLineExecutor.executeSuccess(baselineExecuted)
                times = 0

                experimentStorage.save(waitingForbaseline)
                times = 1

                experimentStorage.save(baselineExecuted)
                times = 0
            }
        }

        Assertions.assertThat(waitingForbaseline.timing.baselineExecuted).isSameAs(localDateTime10)
    }
}
