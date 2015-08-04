package org.craftsmenlabs.gareth.api.model;

import lombok.Data;

/**
 * Created by hylke on 04/08/15.
 */
@Data
public class AssumptionBlock {

    private String baseline, assumption, time, success, failure;

}
