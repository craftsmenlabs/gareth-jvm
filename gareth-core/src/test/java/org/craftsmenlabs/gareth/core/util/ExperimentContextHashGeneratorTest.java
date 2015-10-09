package org.craftsmenlabs.gareth.core.util;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by hylke on 23/09/15.
 */
public class ExperimentContextHashGeneratorTest {

    @Mock
    private ExperimentContext mockExperimentContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateHashWithNull() throws Exception {
        final String expectedHash = "cd372fb85148700fa88095e3492d3f9f5beb43e555e5ff26d95f5a6adc36f8e6";
        assertEquals(expectedHash, ExperimentContextHashGenerator.generateHash(null));
        assertEquals(expectedHash, ExperimentContextHashGenerator.generateHash(null));
    }

    @Test
    public void testGenerateHashWith() throws Exception {
        final String[] surrogateKey = {"a", "b", "c"};
        final String expectedHash1 = ExperimentContextHashGenerator.generateHash(surrogateKey);
        final String expectedHash2 = ExperimentContextHashGenerator.generateHash(surrogateKey);
        assertNotNull(expectedHash1);
        assertNotNull(expectedHash2);
        assertEquals(expectedHash1, expectedHash2);
    }

    @Test
    public void testGenerateHashWithNonEqualKeys() throws Exception {
        when(mockExperimentContext.getExperimentName()).thenReturn("experiment");
        final String[] surrogateKey1 = {"a", "b", "c"};
        final String[] surrogateKey2 = {"d", "e", "f"};
        final String expectedHash1 = ExperimentContextHashGenerator.generateHash(surrogateKey1);
        final String expectedHash2 = ExperimentContextHashGenerator.generateHash(surrogateKey2);
        assertNotNull(expectedHash1);
        assertNotNull(expectedHash2);
        assertNotEquals(expectedHash1, expectedHash2);
    }
}