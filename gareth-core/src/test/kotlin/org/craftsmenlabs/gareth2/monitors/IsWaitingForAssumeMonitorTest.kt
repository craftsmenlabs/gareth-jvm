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
import org.craftsmenlabs.gareth2.time.DurationCalculator
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import rx.lang.kotlin.toObservable
import java.time.Duration
import java.time.LocalDateTime

class IsWaitingForAssumeMonitorTest {

    val MILLTI_DELAY: Long = 3
    val localDateTime1 = LocalDateTime.now().minusHours(1)
    val localDateTime2 = LocalDateTime.now().minusHours(2)
    val localDateTime3 = LocalDateTime.now().minusHours(3)
    val localDateTime4 = LocalDateTime.now().minusHours(4)
    val localDateTime5 = LocalDateTime.now().minusHours(5)
    val localDateTime6 = LocalDateTime.now().minusHours(6)
    val localDateTime7 = LocalDateTime.now().minusHours(7)
    val localDateTime8 = LocalDateTime.now().minusHours(8)
    val localDateTime9 = LocalDateTime.now().minusHours(8)
    val localDateTime10 = LocalDateTime.now().minusHours(10)
    val localDateTime11 = LocalDateTime.now().minusHours(11)
    val localDateTime12 = LocalDateTime.now().minusHours(12)

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: DateTimeService

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    @Injectable
    lateinit var durationCalculator: DurationCalculator

    lateinit var monitor: IsWaitingForAssumeMonitor

    @Before
    fun setUp() {
        monitor = IsWaitingForAssumeMonitor(experimentProvider, dateTimeService, experimentStorage, durationCalculator)
    }

    @Test
    @Ignore("You have to fix _all_ unit test, because experiments became immutable. the IT test works though :)")
    fun shouldOnlyOperateOnStartedExperiments() {
        val details = ExperimentDetails("id", "baseline", "assumption", "time", "success", "failure", 111)

        val timingBaselineExecuted = ExperimentTiming(localDateTime1, localDateTime2, localDateTime3, localDateTime4, localDateTime5)
        val timingWaitingForAssume = ExperimentTiming(localDateTime6, localDateTime7, localDateTime8, localDateTime9, localDateTime10, localDateTime11)

        val results = ExperimentResults()
        val experimentBaseline = Experiment(details, timingBaselineExecuted, results, "id")
        val experimentWaitingForAssume = Experiment(details, timingWaitingForAssume, results, "id")
        val experiments = listOf(experimentBaseline, experimentWaitingForAssume)


        val duration = Duration.ofMillis(MILLTI_DELAY)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                dateTimeService.now()
                result = arrayListOf(localDateTime5, localDateTime12)

                durationCalculator.getDuration(experimentBaseline)
                result = duration
            }
        }

        monitor.start();

        Thread.sleep(MILLTI_DELAY * 20)

        object : Verifications() {
            init {
                experimentStorage.save(experimentBaseline)
                times = 1

                experimentStorage.save(experimentWaitingForAssume)
                times = 0
            }
        }

        assertThat(experimentBaseline.timing.waitingForAssume).isSameAs(localDateTime12)
    }
}
