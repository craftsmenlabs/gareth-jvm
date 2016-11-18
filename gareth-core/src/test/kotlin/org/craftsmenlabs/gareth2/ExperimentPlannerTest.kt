package org.craftsmenlabs.gareth2

import mockit.*
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentRun
import org.craftsmenlabs.gareth2.rx.KSchedulers
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.craftsmenlabs.gareth2.time.DurationCalculator
import org.junit.Before
import org.junit.Test
import rx.schedulers.Schedulers
import java.time.Duration
import java.time.LocalDateTime

class ExperimentPlannerTest {

    val TEST_ID = "TEST_ID"

    @Injectable
    lateinit var experimentPersistence: ExperimentPersistence

    @Injectable
    lateinit var glueLineExecutor: GlueLineExecutor

    @Injectable
    lateinit var durationCalculator: DurationCalculator

    @Injectable
    lateinit var dateTimeService: DateTimeService

    @Tested
    lateinit var planner: ExperimentPlanner

    @Mocked
    lateinit var kSchedulers: KSchedulers.Companion

    @Before
    fun setUp() {

        object : Expectations() {
            init {
                KSchedulers.io();
                result = Schedulers.immediate()
                minTimes = 0;
            }
        }
    }

    @Test
    fun shouldInvokeLoading_whenConstructedBySpring() {
        object : Verifications() {
            init {
                experimentPersistence.getAllFinalizingExperiments()
                experimentPersistence.getAllRunningExperiments()
            }
        }
    }

    @Test
    fun shouldExecuteBaselineAndSaveRun_WhenExperimentIsStarted(
            @Injectable experiment: Experiment,
            @Injectable experimentRun: ExperimentRun) {

        val duration = Duration.ofSeconds(10)
        val now = LocalDateTime.now()

        object : Expectations() {
            init {
                experiment.id
                result = TEST_ID

                dateTimeService.now()
                result = now

                experimentPersistence.getExperimentRun(experiment)
                result = null;

                durationCalculator.getDuration(experiment)
                result = duration
            }
        }

        planner.startExperiment(experiment)

        var experimentRuns = mutableListOf<ExperimentRun>()
        object : Verifications() {
            init {
                glueLineExecutor.executeBaseline(experiment)
//                experimentPersistence.saveExperimentRun(withCapture(experimentRuns))
                experimentPersistence.saveExperimentRun(withAny(experimentRun))
            }
        }

        // TODO with capture doesn't seem to work...
//        assertThat(experimentRuns).hasSize(1)
//        assertThat(experimentRuns[0].experimentId).isEqualTo(TEST_ID)
//        assertThat(experimentRuns[0].baselineExecuted).isEqualTo(now)
//        assertThat(experimentRuns[0].assumptionPlanned).isEqualTo(now.plus(duration))

    }

    @Test
    fun shouldDelayAssumption_WhenExperimentIsContinued(
            @Injectable experiment: Experiment,
            @Injectable experimentRun: ExperimentRun) {

        object : Expectations() {
            init {
                experimentRun.assumptionPlanned
            }
        }

        planner.continueExperiment(experiment, experimentRun)
    }
}
