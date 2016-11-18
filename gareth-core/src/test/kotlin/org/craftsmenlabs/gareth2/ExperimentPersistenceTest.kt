package org.craftsmenlabs.gareth2

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import mockit.Verifications
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentRun
import org.junit.Test
import java.time.LocalDateTime

class ExperimentPersistenceTest {

    @Injectable
    lateinit var storage: ExperimentStorage;

    @Tested
    lateinit var persistence: ExperimentPersistence

    @Test
    fun saveShouldForwardToStorage_whenSaveExperimentInvoked(@Injectable experiment: Experiment) {

        persistence.saveExperiment(experiment);

        object : Verifications() {
            init {
                storage.save(experiment)
            }
        }
    }

    @Test
    fun saveShouldForwardToStorage_whenSaveExperimentRunInvoked(@Injectable experimentRun: ExperimentRun) {

        persistence.saveExperimentRun(experimentRun);

        object : Verifications() {
            init {
                storage.save(experimentRun)
            }
        }
    }

    @Test
    fun shouldReturnOnlyRunForExperiment_whenSeveralExperimentRunsAreStored(
            @Injectable experimentA: Experiment,
            @Injectable experimentRunB: ExperimentRun,
            @Injectable experimentRunA: ExperimentRun) {

        object : Expectations() {init {
            experimentA.id
            result = "A"

            experimentRunA.experimentId
            result = "A"

            experimentRunB.experimentId
            result = "B"

            storage.loadAllRuns()
            result = listOf(experimentRunB, experimentRunA);
        }
        }

        val actual = persistence.getExperimentRun(experimentA)

        assertThat(actual).isSameAs(experimentRunA)
    }

    @Test
    fun shouldReturnOnlyNull_whenSeveralNonMatchingExperimentRunsAreStored(
            @Injectable experimentA: Experiment,
            @Injectable experimentRunB: ExperimentRun,
            @Injectable experimentRunC: ExperimentRun) {

        object : Expectations() {init {
            experimentA.id
            result = "A"

            experimentRunB.experimentId
            result = "B"

            experimentRunC.experimentId
            result = "B"

            storage.loadAllRuns()
            result = listOf(experimentRunB, experimentRunC);
        }
        }

        val actual = persistence.getExperimentRun(experimentA)

        assertThat(actual).isNull()
    }

    @Test
    fun shouldOnlyReturnRunsForExperimentsPlannedInTheFuture_whenGetAllRunningIsInvoked(
            @Injectable experimentA: Experiment,
            @Injectable experimentB: Experiment,
            @Injectable experimentC: Experiment,
            @Injectable experimentD: Experiment,
            @Injectable experimentRunABefore: ExperimentRun,
            @Injectable experimentRunAAfter: ExperimentRun,
            @Injectable experimentRunBAfter: ExperimentRun,
            @Injectable experimentRunCBefore: ExperimentRun) {

        object : Expectations() {
            init {
                experimentA.id
                result = "A"

                experimentRunABefore.experimentId
                result = "A"

                experimentRunABefore.assumptionPlanned
                result = LocalDateTime.now().minusDays(1)

                experimentRunAAfter.experimentId
                result = "A"

                experimentRunAAfter.assumptionPlanned
                result = LocalDateTime.now().plusDays(1)

                experimentB.id
                result = "B"

                experimentRunBAfter.experimentId
                result = "B"

                experimentRunBAfter.assumptionPlanned
                result = LocalDateTime.now().plusDays(1)

                experimentC.id
                result = "C"

                experimentRunCBefore.experimentId
                result = "C"

                experimentRunCBefore.assumptionPlanned
                result = LocalDateTime.now().minusDays(1)

                experimentD.id
                result = "D"

                storage.loadAllRuns()
                result = listOf(experimentRunAAfter, experimentRunBAfter, experimentRunABefore, experimentRunCBefore);

                storage.loadAllExperiments()
                result = listOf(experimentA, experimentB, experimentC, experimentD)
            }
        }

        val runningExperiments = persistence.getAllRunningExperiments()

        assertThat(runningExperiments).hasSize(2)
        assertThat(runningExperiments.find { it.first.id == "A" }?.second).isSameAs(experimentRunAAfter);
        assertThat(runningExperiments.find { it.first.id == "B" }?.second).isSameAs(experimentRunBAfter);
    }


    @Test
    fun shouldOnlyReturnRunsForExperimentsWhichHaveBeenExecutedButNotFinilized_whenGetAllFinilizingIsInvoked(
            @Injectable experimentA: Experiment,
            @Injectable experimentB: Experiment,
            @Injectable experimentC: Experiment,
            @Injectable experimentRunAExecuted: ExperimentRun,
            @Injectable experimentRunBFinalized: ExperimentRun,
            @Injectable experimentRunCRunning: ExperimentRun) {

        object : Expectations() {
            init {
                experimentA.id
                result = "A"

                experimentRunAExecuted.experimentId
                result = "A"

                experimentRunAExecuted.assumptionExecuted
                result = LocalDateTime.now()

                experimentRunAExecuted.completionExecuted
                result = null


                experimentB.id
                result = "B"

                experimentRunBFinalized.experimentId
                result = "B"

                experimentRunBFinalized.assumptionExecuted
                result = LocalDateTime.now()

                experimentRunBFinalized.completionExecuted
                result = LocalDateTime.now()


                experimentC.id
                result = "C"

                experimentRunCRunning.experimentId
                result = "C"

                experimentRunCRunning.assumptionExecuted
                result = null


                storage.loadAllRuns()
                result = listOf(experimentRunAExecuted, experimentRunBFinalized, experimentRunCRunning);

                storage.loadAllExperiments()
                result = listOf(experimentA, experimentB, experimentC)
            }
        }

        val finalizingExperiments = persistence.getAllFinalizingExperiments()

        assertThat(finalizingExperiments).hasSize(1)
        assertThat(finalizingExperiments.find { it.first.id == "A" }?.second).isSameAs(experimentRunAExecuted);


    }

}