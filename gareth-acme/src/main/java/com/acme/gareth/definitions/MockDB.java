package com.acme.gareth.definitions;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MockDB {

    private Map<String, Double> productionNow = ImmutableMap.of("apples", 50.0, "bananas", 30.0, "peaches", 15.0);
    private Map<String, Double> productionLater = ImmutableMap.of("apples", 55.0, "bananas", 24.0, "peaches", 20.0);

    public double getSalesForProductAtBaseline(String product) {
        if (!productionNow.containsKey(product)) {
            throw new IllegalArgumentException(("not a valid product"));
        }
        return productionNow.get(product);
    }

    public double getSalesForProductAtAssume(String product) {
        if (!productionLater.containsKey(product)) {
            throw new IllegalArgumentException(("not a valid product"));
        }
        return productionLater.get(product);
    }


}
