package org.craftsmenlabs.gareth.api;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;

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
     * Stop experiments and if necessary persist the engine state
     */
    void stop();


    /**
     * Get list of experiment context
     *
     * @return list of loaded experiment contexts
     */
    List<ExperimentContext> getExperimentContexts();


    /**
     * Get a list with experiment runs
     *
     * @return
     */
    List<ExperimentRunContext> getExperimentRunContexts();

    /**
     * Find experiment run contexts for hash
     *
     * @param hash
     * @return
     */
    List<ExperimentRunContext> findExperimentRunContextsForHash(final String hash);

    /**
     * (re-)plan a experiment based on experiment context.
     *
     * @param experimentContext
     */
    void planExperimentContext(final ExperimentContext experimentContext);


    /**
     * Find a experiment based on hash
     *
     * @param hash generated hash for experiment context
     * @return Experiment Context
     * @throws GarethUnknownExperimentException if experiment for the hash cannot be found
     */
    ExperimentContext findExperimentContextForHash(final String hash) throws GarethUnknownExperimentException;


    /**
     * Returns if the engine is started
     *
     * @return
     */
    boolean isStarted();
}
