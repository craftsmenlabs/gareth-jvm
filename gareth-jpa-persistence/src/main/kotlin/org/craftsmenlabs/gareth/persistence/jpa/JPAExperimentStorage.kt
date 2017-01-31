package org.craftsmenlabs.gareth.persistence.jpa

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment


class JPAExperimentStorage : ExperimentStorage {
    override fun loadAllExperiments(): List<Experiment> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(experiment: Experiment) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setListener(listener: ((Experiment) -> Unit)?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getById(id: String): Experiment {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}