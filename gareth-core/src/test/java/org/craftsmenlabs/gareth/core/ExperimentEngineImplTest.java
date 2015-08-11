package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
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
import java.util.ArrayList;
import java.util.List;

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

    @Mock
    private ExperimentEngineConfig mockExperimentEngineConfig;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockExperimentEngineConfig.getDefinitionClasses()).thenReturn(new Class[]{});
        when(mockExperimentEngineConfig.getInputStreams()).thenReturn(new InputStream[]{});
        experimentEngine = new ExperimentEngineImpl
                .Builder(mockExperimentEngineConfig)
                .setDefinitionRegistry(mockDefinitionRegistry)
                .setParsedDefinitionFactory(mockParsedDefinitionFactory)
                .setExperimentFactory(mockExperimentFactory)
                .setExperimentRegistry(mockExperimentRegistry)
                .build();
    }

    @Test
    public void testStartValidateParseDefinition() throws Exception {
        final Class clazz = Object.class;
        final Class[] definitionClasses = new Class[]{clazz};

        when(mockExperimentEngineConfig.getDefinitionClasses()).thenReturn(definitionClasses);

        when(mockParsedDefinitionFactory.parse(clazz)).thenReturn(mockParsedDefinition);

        experimentEngine.start();
        verify(mockParsedDefinitionFactory).parse(clazz);
    }

    @Test
    public void testStartValidateParseExperiment() throws Exception {
        final Experiment mockExperiment = mock(Experiment.class);
        when(mockExperiment.getExperimentName()).thenReturn("experiment");
        final InputStream mockInputStream = mock(InputStream.class);
        final InputStream[] inputStreams = new InputStream[]{mockInputStream};

        when(mockExperimentEngineConfig.getInputStreams()).thenReturn(inputStreams);

        when(mockExperimentFactory.buildExperiment(mockInputStream)).thenReturn(mockExperiment);

        experimentEngine.start();
        verify(mockExperimentRegistry).addExperiment("experiment", mockExperiment);
    }
}