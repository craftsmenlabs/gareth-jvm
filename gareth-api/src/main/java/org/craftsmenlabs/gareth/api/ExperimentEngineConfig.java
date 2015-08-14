package org.craftsmenlabs.gareth.api;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hylke on 11/08/15.
 */
public interface ExperimentEngineConfig {

    /**
     * Get definition classes
     * @return
     */
    Class[] getDefinitionClasses();

    /**
     * Get collection of experiment input streamss
     *
     * @return
     */
    InputStream[] getInputStreams();

    /**
     * Whether to ignore invalid definitions
     *
     * @return
     */
    boolean isIgnoreInvalidDefinitions();

    /**
     * Whether to ignore invalid experiments
     * @return
     */
    boolean isIgnoreInvalidExperiments();

    /**
     * Whether baseline invocation are ignored
     *
     * @return
     */
    boolean isIgnoreInvocationExceptions();

}
