package org.craftsmenlabs.gareth.monitors

import mockit.Deencapsulation
import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.Captors
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentTiming
import org.craftsmenlabs.gareth.model.Gluelines
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.DurationCalculator
import org.craftsmenlabs.gareth.time.TimeService
import org.craftsmenlabs.monitorintegration.computationTestOverride
import org.craftsmenlabs.monitorintegration.ioTestOverride
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
import java.time.Duration
import java.time.LocalDateTime

class ExecuteAssumeMonitorTest {

    val MILLI_DELAY: Long = 15
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
    val localDateTime12 = LocalDateTime.now().minusHours(12)
    private val ID1 = "111"
    private val ID2 = "222"

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    @Injectable
    lateinit var durationCalculator: DurationCalculator

    @Injectable
    lateinit var experimentExecutor: ExperimentExecutor

    lateinit var monitor: ExecuteAssumeMonitor

    @Before
    fun setUp() {
        monitor = ExecuteAssumeMonitor(experimentProvider, dateTimeService, experimentStorage, experimentExecutor, durationCalculator)
    }

    @Test
    @Ignore
    fun shouldOnlyOperateOnStartedExperiments(@Mocked schedulers: Schedulers) {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()
        val glueLines = Gluelines("baseline", "assumption", "success", "failure", "time")

        val timingBaselineExecuted = ExperimentTiming(localDateTime1, localDateTime2, localDateTime3, localDateTime4)
        val timingWaitingForAssume = ExperimentTiming(localDateTime6, localDateTime7, localDateTime8, localDateTime9)

        val experimentBaseline = Experiment(ID1, name = "name", glueLines = glueLines, timing = timingBaselineExecuted)
        val experimentWaitingForAssume = Experiment(id = ID2, name = "name", glueLines = glueLines, timing = timingWaitingForAssume)
        val experiments = listOf(experimentBaseline, experimentWaitingForAssume)

        val duration = Duration.ofMillis(MILLI_DELAY)

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

        val storageCaptor = Captors.experimentStorage_save(experimentStorage)

        assertThat(storageCaptor[0]).isEqualTo(
                experimentBaseline.copy(
                        timing = experimentBaseline.timing.copy(baselineExecuted = localDateTime12)))
    }

    @Test
    fun shouldOnlyOperateOnStartedExperimentsOnce() {
        val glueLines = Gluelines("baseline", "assumption", "success", "failure", "time")

        val timingBaselineExecuted = ExperimentTiming(localDateTime1, localDateTime2, localDateTime3, localDateTime4)

        val experimentBaseline = Experiment(id = ID1, name = "name", glueLines = glueLines, timing = timingBaselineExecuted)
        val experiments = listOf(experimentBaseline)

        val duration = Duration.ofMillis(MILLI_DELAY)

        val delayedExperiments: MutableList<String> = Deencapsulation.getField(monitor, "delayedExperiments")
        delayedExperiments.add(ID1)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                dateTimeService.now()
                result = arrayListOf(localDateTime5, localDateTime12)
                maxTimes = 0

                durationCalculator.getDuration(experimentBaseline)
                result = duration
                maxTimes = 0
            }
        }

        monitor.start();
    }
}
