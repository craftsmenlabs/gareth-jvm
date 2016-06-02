package org.craftsmenlabs.gareth.api.model;

import java.util.Optional;

public enum GlueLineType {
    BASELINE, ASSUMPTION, TIME, SUCCESS, FAILURE,;

    public static Optional<GlueLineType> safeValueOf(String key) {
        try {
            return Optional.of(GlueLineType.valueOf(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
