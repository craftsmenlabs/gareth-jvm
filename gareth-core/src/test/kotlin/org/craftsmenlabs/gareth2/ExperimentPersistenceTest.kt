package org.craftsmenlabs.gareth2

import mockit.Injectable
import mockit.Tested
import mockit.Verifications
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentRun
import org.junit.Test

class ExperimentPersistenceTest {

    @Injectable
    lateinit var storage: ExperimentStorage;

    @Tested
    lateinit var persistence: ExperimentPersistence

    @Test
    fun saveShouldForwardToStorage_whenSaveExperimentInvoked(@Injectable experiment: Experiment) {

        persistence.save(experiment);

        object : Verifications() {
            init {
                storage.save(experiment)
            }
        }
    }

    @Test
    fun saveShouldForwardToStorage_whenSaveExperimentRunInvoked(@Injectable experimentRun: ExperimentRun) {

        persistence.save(experimentRun);

        object : Verifications() {
            init {
                storage.save(experimentRun)
            }
        }
    }
}