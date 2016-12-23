package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth2.GluelineLookup
import org.craftsmenlabs.gareth2.model.Experiment


class GlueLineLookupRestClient : GluelineLookup{
    override fun isExperimentReady(experiment: Experiment): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isLineReady(glueline: String): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}