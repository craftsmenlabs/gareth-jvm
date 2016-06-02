package org.craftsmenlabs.gareth.api.model;

import lombok.Data;

@Data
public class AssumptionBlock {

    private String baseline, assumption, time, success, failure;

}
