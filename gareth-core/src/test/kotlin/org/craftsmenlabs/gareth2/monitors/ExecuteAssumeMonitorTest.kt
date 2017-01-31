package org.craftsmenlabs.gareth2.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Verifications
import org.assertj.core.api.Assertions
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentDetails
import org.craftsmenlabs.gareth.model.ExperimentResults
import org.craftsmenlabs.gareth.model.ExperimentTiming
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import rx.lang.kotlin.toObservable
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
    lateinit var dateTimeService: DateTimeService

    @Injectable
    lateinit var glueLineExecutor: GlueLineExecutor

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: ExecuteAssumeMonitor

    @Before
    fun setUp() {
        monitor = ExecuteAssumeMonitor(experimentProvider, dateTimeService, experimentStorage, glueLineExecutor)
    }

    @Test
    @Ignore("You have to fix _all_ unit test, because experiments became immutable. the IT test works though :)")
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
        val waitingForassume = Experiment(details, timingFinalisationExecuted, results, "id")
        val assumeExecuted = Experiment(details, timingCompleted, results, "id")
        val experiments = listOf(waitingForassume, assumeExecuted)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                dateTimeService.now()
                result = localDateTime14
            }
        }

        monitor.start();

        object : Verifications() {
            init {
                glueLineExecutor.executeAssume(waitingForassume)
                times = 1

                glueLineExecutor.executeAssume(assumeExecuted)
                times = 0

                experimentStorage.save(waitingForassume)
                times = 1

                experimentStorage.save(assumeExecuted)
                times = 0
            }
        }

        Assertions.assertThat(waitingForassume.timing.assumeExecuted).isSameAs(localDateTime14)
    }
}
