package org.craftsmenlabs.gareth.api;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;

import java.io.InputStream;
import java.util.List;

/**
 * Created by hylke on 10/08/15.
 */
public interface ExperimentEngine {

    /**
     * Load definitions, experiments and executes the experiments
     */
    void start();


    /**
     * Get list of experiment context
     *
     * @return list of loaded experiment contexts
     */
    List<ExperimentContext> getExperimentContexts();

    /**
     * (re-)plan a experiment based on experiment context.
     *
     * @param experimentContext
     */
    void planExperimentContext(final ExperimentContext experimentContext);


    /**
     * Returns if the engine is started
     *
     * @return
     */
    boolean isStarted();
}
