package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentRun
import org.craftsmenlabs.gareth2.rx.KSchedulers
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.craftsmenlabs.gareth2.time.DurationCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.lang.kotlin.toSingletonObservable
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
class ExperimentPlanner @Autowired constructor(
        private val experimentPersistence: ExperimentPersistence,
        private val glueLineExecutor: GlueLineExecutor,
        private val durationCalculator: DurationCalculator,
        private val dateTimeService: DateTimeService) {

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
                .observeOn(KSchedulers.io())
                .subscribe {
                    val duration = durationCalculator.getDuration(experiment)
                    glueLineExecutor.executeBaseline(experiment)
                    val experimentRun = ExperimentRun(experiment.id, dateTimeService.now(), dateTimeService.now().plus(duration))
                    experimentPersistence.saveExperimentRun(experimentRun)
                    continueExperiment(experiment, experimentRun)
                }
    }

    internal fun continueExperiment(experiment: Experiment, experimentRun: ExperimentRun) {
        val secondsToGo = ChronoUnit.SECONDS.between(dateTimeService.now(), experimentRun.assumptionPlanned)

        toSingletonObservable()
                .delay(secondsToGo, TimeUnit.SECONDS)
                .observeOn(KSchedulers.io())
                .subscribe {
                    val success = glueLineExecutor.executeAssumption(experiment)
                    experimentRun.assumptionExecuted = dateTimeService.now()
                    experimentRun.success = success
                    experimentPersistence.saveExperimentRun(experimentRun)

                    finalizeExperiment(experiment, experimentRun)
                }
    }

    private fun finalizeExperiment(experiment: Experiment, experimentRun: ExperimentRun) {
        val success = experimentRun.success ?: return

        toSingletonObservable()
                .observeOn(KSchedulers.io())
                .subscribe {
                    if (success) {
                        glueLineExecutor.executeSuccess(experiment)
                    } else {
                        glueLineExecutor.executeFailure(experiment)
                    }

                    experimentRun.completionExecuted = dateTimeService.now()
                    experimentPersistence.saveExperimentRun(experimentRun)
                }
    }
}
