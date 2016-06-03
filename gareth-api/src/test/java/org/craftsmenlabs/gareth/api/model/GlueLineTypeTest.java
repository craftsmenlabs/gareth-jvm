package org.craftsmenlabs.gareth.api.model;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by hylke on 03/06/16.
 */
public class GlueLineTypeTest {


    @Test
    public void safeValueOfExistingValue() throws Exception {
        final Optional<GlueLineType> glueLineType = GlueLineType.safeValueOf("BASELINE");
        assertTrue(glueLineType.isPresent());
        assertEquals(GlueLineType.BASELINE, glueLineType.get());
    }

    @Test
    public void safeValueOfNonExistingValue() throws Exception {
        final Optional<GlueLineType> glueLineType = GlueLineType.safeValueOf("NONEXISTING");
        assertFalse(glueLineType.isPresent());
    }

    @Test
    public void safeValueOfWithNull() throws Exception {
        final Optional<GlueLineType> glueLineType = GlueLineType.safeValueOf(null);
        assertFalse(glueLineType.isPresent());
    }

}