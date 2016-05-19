package org.craftsmenlabs.gareth.api.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Experiment {

    private String experimentName;
    private List<AssumptionBlock> assumptionBlockList = new ArrayList<AssumptionBlock>();


}
