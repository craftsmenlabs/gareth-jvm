package org.craftsmenlabs.gareth.core;

import mockit.Verifications;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.core.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.core.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinition;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.core.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.persist.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistry;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExperimentEngineTest {

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
    private DefaultAssumeScheduler mockAssumeScheduler;

    @Mock
    private MethodInvoker mockMethodInvoker;

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
        experimentEngine = new ExperimentEngineBuilder(mockExperimentEngineConfig)
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

        List<Class> classList = new ArrayList<>();
        new Verifications(){{
            mockParsedDefinitionFactory.parse(withCapture(classList));
        }};

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
        final ExperimentContext mockExperimentContext = mock(ExperimentContext.class);
        when(mockExperimentContext.isValid()).thenReturn(false);
        experimentEngine.planExperimentContext(mockExperimentContext);
        verify(mockAssumeScheduler, never()).schedule(any(ExperimentRunContext.class), any(ExperimentEngine.class));
    }

    @Test
    public void testPlanExperimentContext() {
        experimentEngine.start();
        final ExperimentContext mockExperimentContext = mock(ExperimentContext.class);
        final MethodDescriptor mockMethodDescriptor = mock(MethodDescriptor.class);
        when(mockExperimentContext.getBaseline()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getAssume()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getSuccess()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getFailure()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.isValid()).thenReturn(true);
        experimentEngine.planExperimentContext(mockExperimentContext);
        verify(mockAssumeScheduler).schedule(any(ExperimentRunContext.class), any(ExperimentEngine.class));
    }

    @Test
    public void testPlanExperimentContextWithStoppedEngine() {
        try {
            final ExperimentContext mockExperimentRunContext = mock(ExperimentContext.class);
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
        final ExperimentContext experimentContext = experimentEngine.findExperimentContextForHash(hash);
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

        final ExperimentRunContext experimentRunContext1 = mock(ExperimentRunContext.class);
        final ExperimentRunContext experimentRunContext2 = mock(ExperimentRunContext.class);

        when(experimentRunContext1.getHash()).thenReturn("hash-1");
        when(experimentRunContext2.getHash()).thenReturn("hash-2");

        experimentEngine.start();
        experimentEngine.getExperimentRunContexts().add(experimentRunContext1);
        experimentEngine.getExperimentRunContexts().add(experimentRunContext2);

        final List<ExperimentRunContext> experimentRunContexts = experimentEngine
                .findExperimentRunContextsForHash("hash-1");
        assertNotNull(experimentRunContexts);
        assertEquals(1, experimentRunContexts.size());
        assertEquals(experimentRunContext1, experimentRunContexts.get(0));
    }


}