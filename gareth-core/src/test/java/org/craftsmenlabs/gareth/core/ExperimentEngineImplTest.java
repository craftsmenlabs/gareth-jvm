package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.api.registry.ExperimentRegistry;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

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

    @Mock
    private AssumeScheduler mockAssumeScheduler;

    @Mock
    private MethodInvoker mockMethodInvoker;

    @Mock
    private RestServiceFactory mockRestServiceFactory;

    @Mock
    private ExperimentEnginePersistence mockExperimentEnginePersistence;

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
                .setAssumeScheduler(mockAssumeScheduler)
                .setMethodInvoker(mockMethodInvoker)
                .setExperimentEnginePersistence(mockExperimentEnginePersistence)
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


    @Test
    public void testPlanExperimentContextInvalidExperimentContext() {
        experimentEngine.start();
        final ExperimentContext mockExperimentContext = mock(ExperimentContext.class);
        when(mockExperimentContext.isValid()).thenReturn(false);
        experimentEngine.planExperimentContext(mockExperimentContext);
        verify(mockAssumeScheduler, never()).schedule(mockExperimentContext);
    }

    @Test
    public void testPlanExperimentContext() {
        experimentEngine.start();
        final ExperimentContext mockExperimentContext = mock(ExperimentContext.class);
        when(mockExperimentContext.isValid()).thenReturn(true);
        when(mockExperimentContext.getBaselineState()).thenReturn(ExperimentPartState.OPEN);
        when(mockExperimentContext.getAssumeState()).thenReturn(ExperimentPartState.OPEN);
        experimentEngine.planExperimentContext(mockExperimentContext);
        verify(mockAssumeScheduler).schedule(mockExperimentContext);
    }

    @Test
    public void testPlanExperimentContextWithStoppedEngine() {
        try {
            final ExperimentContext mockExperimentContext = mock(ExperimentContext.class);
            experimentEngine.planExperimentContext(mockExperimentContext);
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("Cannot plan experiment context when engine is not started"));
        }
    }

    @Test
    public void testStartTwice() {
        try {
            experimentEngine.start();
            experimentEngine.start();
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("Experiment engine already started"));
        }
    }

    @Test
    public void testStopExperimentEngineWhenNotStarted() {
        try {
            experimentEngine.stop();
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("Experiment engine is not started"));
        }
    }

    @Test
    public void testStopWithoutPersistence() throws Exception {
        experimentEngine.start();
        experimentEngine.stop();
        verify(mockExperimentEnginePersistence).persist(experimentEngine);
    }


}