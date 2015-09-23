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
        final String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        assertEquals(expectedHash, ExperimentContextHashGenerator.generateHash(null));
        assertEquals(expectedHash, ExperimentContextHashGenerator.generateHash(null));
    }

    @Test
    public void testGenerateHashWithEmptyExperimentContext() throws Exception {
        final String expectedHash1 = ExperimentContextHashGenerator.generateHash(mockExperimentContext);
        final String expectedHash2 = ExperimentContextHashGenerator.generateHash(mockExperimentContext);
        assertNotNull(expectedHash1);
        assertNotNull(expectedHash2);
        assertEquals(expectedHash1, expectedHash2);
    }

    @Test
    public void testGenerateHashWith() throws Exception {
        when(mockExperimentContext.getExperimentName()).thenReturn("experiment");
        final String expectedHash1 = ExperimentContextHashGenerator.generateHash(mockExperimentContext);
        final String expectedHash2 = ExperimentContextHashGenerator.generateHash(mockExperimentContext);
        assertNotNull(expectedHash1);
        assertNotNull(expectedHash2);
        assertEquals(expectedHash1, expectedHash2);
    }
}