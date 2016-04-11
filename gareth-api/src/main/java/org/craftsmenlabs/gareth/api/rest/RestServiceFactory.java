package org.craftsmenlabs.gareth.api.rest;

import org.craftsmenlabs.gareth.api.ExperimentEngine;

/**
 * Created by hylke on 19/08/15.
 */
public interface RestServiceFactory {

    /**
     * Create a rest service based on a experiment engine.
     *
     * @param experimentEngine
     * @param port
     * @return
     */
    RestService create(final ExperimentEngine experimentEngine, int port);
}
