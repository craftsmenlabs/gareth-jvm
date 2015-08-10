package org.craftsmenlabs.gareth.api.definition;

import org.craftsmenlabs.gareth.api.annotation.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 10/08/15.
 */
public class DefinitionTypeTest {

    @Test
    public void testDefinitionTypeCount() {
        assertEquals(5, DefinitionType.values().length);
    }

    @Test
    public void testGetDefinitionTypeBaseline() throws Exception {
        assertEquals(DefinitionType.BASELINE, DefinitionType.getDefinitionType(Baseline.class));
    }

    @Test
    public void testGetDefinitionTypeAssume() throws Exception {
        assertEquals(DefinitionType.ASSUME, DefinitionType.getDefinitionType(Assume.class));
    }

    @Test
    public void testGetDefinitionTypeTime() throws Exception {
        assertEquals(DefinitionType.TIME, DefinitionType.getDefinitionType(Time.class));
    }

    @Test
    public void testGetDefinitionTypeSuccess() throws Exception {
        assertEquals(DefinitionType.SUCCESS, DefinitionType.getDefinitionType(Success.class));
    }

    @Test
    public void testGetDefinitionTypeFailure() throws Exception {
        assertEquals(DefinitionType.FAILURE, DefinitionType.getDefinitionType(Failure.class));
    }

    @Test
    public void testGetDefinitionTypeWithUnknownClass() throws Exception {
        try {
            DefinitionType.getDefinitionType(java.lang.Object.class);
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertEquals("DefinitionType not found for class class java.lang.Object", e.getMessage());
        }
    }
}