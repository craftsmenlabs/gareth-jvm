package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentRun
import org.craftsmenlabs.gareth2.time.DurationCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.lang.kotlin.toSingletonObservable
import rx.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
class ExperimentPlanner @Autowired constructor(val experimentPersistence: ExperimentPersistence, val glueLineExecutor: GlueLineExecutor, val durationCalculator: DurationCalculator) {

    @PostConstruct
    fun planStoredRunningExperiments() {
        experimentPersistence.getAllFinalizingExperiments().forEach { finalizeExperiment(it.first, it.second) }
        experimentPersistence.getAllRunningExperiments().forEach { continueExperiment(it.first, it.second) }
    }

    fun startExperiment(experiment: Experiment) {
        if (experimentPersistence.getExperimentRun(experiment) != null) {
            return
        }

        toSingletonObservable()
                .observeOn(Schedulers.io())
                .subscribe {
                    val duration = durationCalculator.getDuration(experiment)
                    glueLineExecutor.executeBaseline(experiment)
                    val experimentRun = ExperimentRun(experiment.id, LocalDateTime.now(), LocalDateTime.now().plus(duration))
                    experimentPersistence.save(experimentRun)
                    continueExperiment(experiment, experimentRun)
                }
    }

    private fun continueExperiment(experiment: Experiment, experimentRun: ExperimentRun) {
        val secondsToGo = ChronoUnit.SECONDS.between(LocalDateTime.now(), experimentRun.assumptionPlanned)

        toSingletonObservable()
                .delay(secondsToGo, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe {
                    val success = glueLineExecutor.executeAssumption(experiment)
                    experimentRun.assumptionExecuted = LocalDateTime.now()
                    experimentRun.success = success
                    experimentPersistence.save(experimentRun)

                    finalizeExperiment(experiment, experimentRun)
                }
    }

    private fun finalizeExperiment(experiment: Experiment, experimentRun: ExperimentRun) {
        val success = experimentRun.success ?: return

        toSingletonObservable()
                .observeOn(Schedulers.io())
                .subscribe {
                    if (success) {
                        glueLineExecutor.executeSuccess(experiment)
                    } else {
                        glueLineExecutor.executeFailure(experiment)
                    }

                    experimentRun.completionExecuted = LocalDateTime.now()
                    experimentPersistence.save(experimentRun)
                }
    }
}