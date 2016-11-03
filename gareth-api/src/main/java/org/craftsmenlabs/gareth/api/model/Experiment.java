package org.craftsmenlabs.gareth.api.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parsed experiment file, corresponding to the Gareth grammar
 */
@Data
public class Experiment {
    private String experimentName;
    private int weight;
    private List<AssumptionBlock> assumptionBlockList = new ArrayList<AssumptionBlock>();
}
