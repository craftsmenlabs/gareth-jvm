package org.craftsmenlabs.gareth.core.util;

import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class ExperimentContextHashGeneratorTest {

    @Mock
    private ExperimentContext mockExperimentContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateHashWithNull() throws Exception {
        final String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
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