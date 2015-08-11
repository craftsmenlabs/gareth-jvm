package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.api.registry.ExperimentRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hylke on 11/08/15.
 */
public class ExperimentEngineImplTest {

    private ExperimentEngine experimentEngine;

    @Mock
    private DefinitionRegistry mockDefinitionRegistry;

    @Mock
    private ParsedDefinitionFactory mockParsedDefinitionFactory;

    @Mock
    private ExperimentFactory mockExperimentFactory;

    @Mock
    private ExperimentRegistry mockExperimentRegistry;


    @Mock
    private ParsedDefinition mockParsedDefinition;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        experimentEngine = new ExperimentEngineImpl
                .Builder()
                .setDefinitionRegistry(mockDefinitionRegistry)
                .setParsedDefinitionFactory(mockParsedDefinitionFactory)
                .setExperimentFactory(mockExperimentFactory)
                .setExperimentRegistry(mockExperimentRegistry)
                .build();
    }

    @Test
    public void testRegisterDefinition() throws Exception {
        final Class clazz = Object.class;
        when(mockParsedDefinitionFactory.parse(clazz)).thenReturn(mockParsedDefinition);

        experimentEngine.registerDefinition(clazz);
        verify(mockParsedDefinitionFactory).parse(clazz);
    }

    @Test
    public void testRegisterExperiment() throws Exception {
        final InputStream mockInputStream = mock(InputStream.class);
        final Experiment mockExperiment = mock(Experiment.class);

        when(mockExperiment.getExperimentName()).thenReturn("mock experiment");
        when(mockExperimentFactory.buildExperiment(mockInputStream)).thenReturn(mockExperiment);

        experimentEngine.registerExperiment(mockInputStream);

        verify(mockExperimentRegistry).addExperiment("mock experiment", mockExperiment);

    }
}