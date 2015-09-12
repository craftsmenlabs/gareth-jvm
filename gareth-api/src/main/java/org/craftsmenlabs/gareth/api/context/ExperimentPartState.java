package org.craftsmenlabs.gareth.api.context;

import lombok.Getter;

/**
 * Created by hylke on 11/09/15.
 */
public enum ExperimentPartState {
    NON_EXISTENT("non_existent"),
    OPEN("open"),
    RUNNING("running"),
    FINISHED("finished"),
    ERROR("error");

    @Getter
    private final String name;

    private ExperimentPartState(final String name) {
        this.name = name;
    }
}
