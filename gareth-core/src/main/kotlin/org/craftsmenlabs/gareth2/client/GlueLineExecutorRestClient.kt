package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.model.Experiment
import java.time.Duration

class GlueLineExecutorRestClient : GlueLineExecutor {

    override fun executeBaseline(experiment: Experiment) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun executeAssumption(experiment: Experiment): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDuration(experiment: Experiment): Duration {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun executeSuccess(experiment: Experiment) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun executeFailure(experiment: Experiment) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}