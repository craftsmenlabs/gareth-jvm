package org.craftsmenlabs.gareth.api.context;

import lombok.Getter;

import java.io.Serializable;


public enum ExperimentPartState implements Serializable {
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
