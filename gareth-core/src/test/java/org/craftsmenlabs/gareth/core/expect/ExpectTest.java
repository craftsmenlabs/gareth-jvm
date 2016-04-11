package org.craftsmenlabs.gareth.core.expect;

import org.craftsmenlabs.gareth.core.expect.exception.ExpectException;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by hylke on 08/09/15.
 */
public class ExpectTest {

    @Test
    public void testFail() throws Exception {
        try {
            Expect.fail();
            fail("Should not reach this point");
        } catch (final ExpectException e) {
            assertNull(e.getMessage());
        }
    }

    @Test
    public void testFailWithMessage() throws Exception {
        try {
            Expect.fail("Message");
            fail("Should not reach this point");
        } catch (final ExpectException e) {
            assertTrue(e.getMessage().contains("Message"));
        }
    }
}