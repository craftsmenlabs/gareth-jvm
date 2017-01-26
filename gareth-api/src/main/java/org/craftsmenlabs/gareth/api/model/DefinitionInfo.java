package org.craftsmenlabs.gareth.api.model;

import lombok.Value;

import java.io.Serializable;

@Value
public class DefinitionInfo implements Serializable {

    private String glueline;

    private String method;

    private String className;
}
