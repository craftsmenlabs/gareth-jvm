package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.core.persist.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.core.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.craftsmenlabs.gareth.core.factory.ExperimentFactoryImpl;
import org.craftsmenlabs.gareth.core.invoker.MethodInvokerImpl;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactoryImpl;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionImpl;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistryImpl;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistryImpl;
import org.craftsmenlabs.gareth.core.scheduler.DefaultAssumeScheduler;
import org.craftsmenlabs.gareth.core.util.ExperimentContextHashGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExperimentEngineImplTest {

    private ExperimentEngineImpl experimentEngine;

    @Mock
    private DefinitionRegistryImpl mockDefinitionRegistry;

    @Mock
    private ParsedDefinitionFactoryImpl mockParsedDefinitionFactory;

    @Mock
    private ExperimentFactoryImpl mockExperimentFactory;

    @Mock
    private ExperimentRegistryImpl mockExperimentRegistry;


    @Mock
    private ParsedDefinitionImpl mockParsedDefinition;

    @Mock
    private ExperimentEngineConfigImpl mockExperimentEngineConfig;

    @Mock
    private DefaultAssumeScheduler mockAssumeScheduler;

    @Mock
    private MethodInvokerImpl mockMethodInvoker;

    @Mock
    private ExperimentEnginePersistence mockExperimentEnginePersistence;

    @Mock
    private ExperimentStateChangeListener mockExperimentStateChangeListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockExperimentEngineConfig.getDefinitionClasses()).thenReturn(new Class[]{});
        when(mockExperimentEngineConfig.getInputStreams()).thenReturn(new InputStream[]{});
        when(mockExperimentEnginePersistence.getExperimentStateChangeListener())
                .thenReturn(mockExperimentStateChangeListener);
        experimentEngine = new ExperimentEngineImplBuilder(mockExperimentEngineConfig)
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
    public void testDefinitionRegistryAvailable() {
        assertThat(experimentEngine.getDefinitionRegistry()).isSameAs(mockDefinitionRegistry);
    }

    @Test
    public void testPlanExperimentContextInvalidExperimentContext() {
        experimentEngine.start();
        final ExperimentContextImpl mockExperimentContext = mock(ExperimentContextImpl.class);
        when(mockExperimentContext.isValid()).thenReturn(false);
        experimentEngine.planExperimentContext(mockExperimentContext);
        verify(mockAssumeScheduler, never()).schedule(any(ExperimentRunContextImpl.class), any(ExperimentEngineImpl.class));
    }

    @Test
    public void testPlanExperimentContext() {
        experimentEngine.start();
        final ExperimentContextImpl mockExperimentContext = mock(ExperimentContextImpl.class);
        final MethodDescriptor mockMethodDescriptor = mock(MethodDescriptor.class);
        when(mockExperimentContext.getBaseline()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getAssume()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getSuccess()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getFailure()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.isValid()).thenReturn(true);
        experimentEngine.planExperimentContext(mockExperimentContext);
        verify(mockAssumeScheduler).schedule(any(ExperimentRunContextImpl.class), any(ExperimentEngineImpl.class));
    }

    @Test
    public void testPlanExperimentContextWithStoppedEngine() {
        try {
            final ExperimentContextImpl mockExperimentRunContext = mock(ExperimentContextImpl.class);
            experimentEngine.planExperimentContext(mockExperimentRunContext);
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
    public void testFindExperimentContextForHash() {
        final Class clazz = Object.class;
        final Class[] definitionClasses = new Class[]{clazz};

        final Experiment experiment = new Experiment();
        final AssumptionBlock assumptionBlock = new AssumptionBlock();
        assumptionBlock.setBaseline("baseline");
        assumptionBlock.setAssumption("assumption");
        assumptionBlock.setTime("time");
        assumptionBlock.setSuccess("success");
        assumptionBlock.setFailure("failure");

        experiment.getAssumptionBlockList().add(assumptionBlock);
        experiment.setExperimentName("experiment");

        final List<Experiment> experimentList = new ArrayList<>();
        experimentList.add(experiment);

        final String[] surrogateKey = {"experiment", "baseline", "assumption", "time", "success", "failure"};
        final String hash = ExperimentContextHashGenerator.generateHash(surrogateKey);

        when(mockExperimentEngineConfig.getDefinitionClasses()).thenReturn(definitionClasses);
        when(mockParsedDefinitionFactory.parse(clazz)).thenReturn(mockParsedDefinition);
        when(mockExperimentRegistry.getAllExperiments()).thenReturn(experimentList);

        experimentEngine.start();
        final ExperimentContextImpl experimentContext = experimentEngine.findExperimentContextForHash(hash);
        assertNotNull(experimentContext);

    }

    @Test
    public void testFindExperimentContextForHashWithNullHash() {
        try {
            experimentEngine.start();
            experimentEngine.findExperimentContextForHash(null);
            fail("Should not reach this point");
        } catch (final Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertTrue(e.getMessage().contains("Hash cannot be null"));
        }
    }

    @Test
    public void testFindExperimentContextUnknownExperimentContext() {
        try {
            experimentEngine.start();
            experimentEngine.findExperimentContextForHash("unknown-hash");
            fail("Should not reach this point");
        } catch (final Exception e) {
            assertTrue(e instanceof GarethUnknownExperimentException);
            assertTrue(e.getMessage().contains("Cannot find experiment context for hash"));
        }
    }

    @Test
    public void testFindExperimentContextEngineNotStarted() {
        try {
            experimentEngine.stop();
            experimentEngine.findExperimentContextForHash(null);
            fail("Should not reach this point");
        } catch (final Exception e) {
            assertTrue(e instanceof IllegalStateException);
            assertTrue(e.getMessage().contains("Experiment engine is not started"));
        }
    }

    @Test
    public void testStopWithoutPersistence() throws Exception {
        experimentEngine.start();
        experimentEngine.stop();
        verify(mockExperimentEnginePersistence, never()).persist(experimentEngine);
    }

    @Test
    public void testFindExperimentRunContextsForHashWithNull() {
        try {
            experimentEngine.findExperimentRunContextsForHash(null);
            fail("Should not reach this point");
        } catch (final IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Hash cannot be null"));
        }
    }

    @Test
    public void testFindExperimentRunContextsForHash() {

        final ExperimentRunContextImpl experimentRunContext1 = mock(ExperimentRunContextImpl.class);
        final ExperimentRunContextImpl experimentRunContext2 = mock(ExperimentRunContextImpl.class);

        when(experimentRunContext1.getHash()).thenReturn("hash-1");
        when(experimentRunContext2.getHash()).thenReturn("hash-2");

        experimentEngine.start();
        experimentEngine.getExperimentRunContexts().add(experimentRunContext1);
        experimentEngine.getExperimentRunContexts().add(experimentRunContext2);

        final List<ExperimentRunContextImpl> experimentRunContexts = experimentEngine
                .findExperimentRunContextsForHash("hash-1");
        assertNotNull(experimentRunContexts);
        assertEquals(1, experimentRunContexts.size());
        assertEquals(experimentRunContext1, experimentRunContexts.get(0));
    }


}