package org.craftsmenlabs.gareth.core;

import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


public class ExperimentEngineConfigTest {

    @Test
    public void testBuildConfigWithNothingSet() {
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfig
                .Builder()
                .build();

        assertFalse(experimentEngineConfig.isIgnoreInvalidDefinitions());
        assertFalse(experimentEngineConfig.isIgnoreInvalidExperiments());
        assertEquals(0, experimentEngineConfig.getDefinitionClasses().length);
        assertEquals(0, experimentEngineConfig.getInputStreams().length);
    }

    @Test
    public void testAddDefinitionClass() throws Exception {
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfig
                .Builder()
                .addDefinitionClass(Object.class)
                .build();

        assertFalse(experimentEngineConfig.isIgnoreInvalidDefinitions());
        assertFalse(experimentEngineConfig.isIgnoreInvalidExperiments());
        assertEquals(1, experimentEngineConfig.getDefinitionClasses().length);
        assertEquals(0, experimentEngineConfig.getInputStreams().length);
    }

    @Test
    public void testAddDefinitionClasses() throws Exception {
        final List<Class> classes = new ArrayList<>();
        classes.add(Object.class);
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfig
                .Builder()
                .addDefinitionClasses(classes)
                .build();

        assertFalse(experimentEngineConfig.isIgnoreInvalidDefinitions());
        assertFalse(experimentEngineConfig.isIgnoreInvalidExperiments());
        assertEquals(1, experimentEngineConfig.getDefinitionClasses().length);
        assertEquals(0, experimentEngineConfig.getInputStreams().length);
    }

    @Test
    public void testAddInputStream() throws Exception {
        final InputStream mockInputStream = mock(InputStream.class);
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfig
                .Builder()
                .addInputStreams(mockInputStream)
                .build();

        assertFalse(experimentEngineConfig.isIgnoreInvalidDefinitions());
        assertFalse(experimentEngineConfig.isIgnoreInvalidExperiments());
        assertEquals(0, experimentEngineConfig.getDefinitionClasses().length);
        assertEquals(1, experimentEngineConfig.getInputStreams().length);
    }

    @Test
    public void testAddInputStreams() throws Exception {
        final InputStream mockInputStream = mock(InputStream.class);
        final List<InputStream> inputStreamList = new ArrayList<>();
        inputStreamList.add(mockInputStream);
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfig
                .Builder()
                .addInputStreams(inputStreamList)
                .build();

        assertFalse(experimentEngineConfig.isIgnoreInvalidDefinitions());
        assertFalse(experimentEngineConfig.isIgnoreInvalidExperiments());
        assertEquals(0, experimentEngineConfig.getDefinitionClasses().length);
        assertEquals(1, experimentEngineConfig.getInputStreams().length);
    }

    @Test
    public void testSetIgnoreInvalidDefinitions() throws Exception {
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfig
                .Builder()
                .setIgnoreInvalidDefinitions(true)
                .build();

        assertTrue(experimentEngineConfig.isIgnoreInvalidDefinitions());
        assertFalse(experimentEngineConfig.isIgnoreInvalidExperiments());
        assertEquals(0, experimentEngineConfig.getDefinitionClasses().length);
        assertEquals(0, experimentEngineConfig.getInputStreams().length);
    }

    @Test
    public void testSetIgnoreInvalidExperiments() throws Exception {
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfig
                .Builder()
                .setIgnoreInvalidExperiments(true)
                .build();

        assertFalse(experimentEngineConfig.isIgnoreInvalidDefinitions());
        assertTrue(experimentEngineConfig.isIgnoreInvalidExperiments());
        assertEquals(0, experimentEngineConfig.getDefinitionClasses().length);
        assertEquals(0, experimentEngineConfig.getInputStreams().length);
    }

    @Test
    public void testSetIgnoreInvocationExceptions() throws Exception {
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfig
                .Builder()
                .setIgnoreInvocationExceptions(true)
                .build();

        assertFalse(experimentEngineConfig.isIgnoreInvalidDefinitions());
        assertTrue(experimentEngineConfig.isIgnoreInvocationExceptions());
        assertEquals(0, experimentEngineConfig.getDefinitionClasses().length);
        assertEquals(0, experimentEngineConfig.getInputStreams().length);
    }

    @Test
    public void testIsIgnoreInvalidExperiments() throws Exception {

    }
}