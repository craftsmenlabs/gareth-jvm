package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentRun
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ExperimentPersistence @Autowired constructor(val experimentStorage: ExperimentStorage) {

    fun getAllRunningExperiments(): List<Pair<Experiment, ExperimentRun>> {
        val allRuns = experimentStorage.loadAllRuns()

        return experimentStorage.loadAllExperiments()
                .map { exp ->
                    Pair(exp, allRuns
                            .filter { it.experimentId == exp.id }
                            .filter { it.assumptionPlanned.isAfter(LocalDateTime.now()) }
                            .firstOrNull())
                }
                .filter { it.second != null }
                .map {
                    Pair(it.first, it.second!!)
                }
    }

    fun getAllFinalizingExperiments(): List<Pair<Experiment, ExperimentRun>> {
        val allRuns = experimentStorage.loadAllRuns()

        return experimentStorage.loadAllExperiments()
                .map { exp ->
                    Pair(exp, allRuns
                            .filter { it.experimentId == exp.id }
                            .filter { it.assumptionExecuted != null }
                            .filter { it.completionExecuted == null }
                            .firstOrNull())
                }
                .filter { it.second != null }
                .map {
                    Pair(it.first, it.second!!)
                }
    }

    fun getExperimentRun(experiment: Experiment): ExperimentRun? {
        return experimentStorage.loadAllRuns()
                .filter { it.experimentId == experiment.id }
                .firstOrNull()
    }

    fun save(experiment: Experiment) {
        experimentStorage.save(experiment)
    }

    fun save(experimentRun: ExperimentRun) {
        experimentStorage.save(experimentRun)
    }
}