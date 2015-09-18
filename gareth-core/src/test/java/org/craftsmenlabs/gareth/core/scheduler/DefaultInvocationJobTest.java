package org.craftsmenlabs.gareth.core.scheduler;

import com.xeiam.sundial.JobContext;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.core.JobExecutionContext;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by hylke on 18/09/15.
 */
public class DefaultInvocationJobTest {


    private DefaultInvocationJob defaultInvocationJob;

    @Mock
    private JobContext mockJobContext;

    @Mock
    private MethodInvoker mockMethodInvoker;

    @Mock
    private ExperimentContext mockExperimentContext;

    @Mock
    private MethodDescriptor mockMethodDescriptor;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        defaultInvocationJob = new DefaultInvocationJob();
        defaultInvocationJob.setJobContext(mockJobContext);
        when(mockJobContext.getRequiredValue("methodInvoker")).thenReturn(mockMethodInvoker);
        when(mockJobContext.getRequiredValue("experimentContext")).thenReturn(mockExperimentContext);


    }

    @Test
    public void testDoRun() throws Exception {
        defaultInvocationJob.doRun();
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.RUNNING);
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.FINISHED);
        verify(mockExperimentContext, never()).setSuccessState(any(ExperimentPartState.class));
        verify(mockExperimentContext).setAssumeRun(any(LocalDateTime.class));
        verify(mockExperimentContext).getAssume();
        verify(mockMethodInvoker).invoke(any(MethodDescriptor.class));
    }

    @Test
    public void testDoRunWithStorage() throws Exception {
        when(mockExperimentContext.hasStorage()).thenReturn(true);
        defaultInvocationJob.doRun();
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.RUNNING);
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.FINISHED);
        verify(mockExperimentContext, never()).setSuccessState(any(ExperimentPartState.class));
        verify(mockExperimentContext).setAssumeRun(any(LocalDateTime.class));
        verify(mockExperimentContext).getAssume();
        verify(mockMethodInvoker).invoke(any(MethodDescriptor.class), any(Storage.class));
    }

    @Test
    public void testDoRunWithSuccess() throws Exception {
        when(mockExperimentContext.getSuccess()).thenReturn(mockMethodDescriptor);
        defaultInvocationJob.doRun();
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.RUNNING);
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.FINISHED);
        verify(mockExperimentContext).setSuccessState(ExperimentPartState.RUNNING);
        verify(mockExperimentContext).setSuccessState(ExperimentPartState.FINISHED);
        verify(mockExperimentContext).setAssumeRun(any(LocalDateTime.class));
        verify(mockExperimentContext).setSuccessRun(any(LocalDateTime.class));
        verify(mockExperimentContext).getAssume();
        verify(mockExperimentContext, times(2)).getSuccess();
        verify(mockExperimentContext, never()).getFailure();
        verify(mockMethodInvoker, times(2)).invoke(any(MethodDescriptor.class));
    }

    @Test
    public void testDoRunWithFailure() throws Exception {
        when(mockExperimentContext.getFailure()).thenReturn(mockMethodDescriptor);
        doThrow(new IllegalArgumentException()).doNothing().when(mockMethodInvoker).invoke(any());
        defaultInvocationJob.doRun();
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.RUNNING);
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.ERROR);
        verify(mockExperimentContext).setFailureState(ExperimentPartState.RUNNING);
        verify(mockExperimentContext).setFailureState(ExperimentPartState.FINISHED);
        verify(mockExperimentContext, never()).setAssumeRun(any(LocalDateTime.class));
        verify(mockExperimentContext, never()).setSuccessRun(any(LocalDateTime.class));
        verify(mockExperimentContext).setFailureRun(any(LocalDateTime.class));
        verify(mockExperimentContext).getAssume();
        verify(mockExperimentContext, never()).getSuccess();
        verify(mockExperimentContext, times(2)).getFailure();
        verify(mockMethodInvoker, times(2)).invoke(any(MethodDescriptor.class));
    }

    @Test
    public void testDoRunWithFailureWithSuccessDefined() throws Exception {
        when(mockExperimentContext.getSuccess()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getFailure()).thenReturn(mockMethodDescriptor);
        doThrow(new IllegalArgumentException()).doNothing().when(mockMethodInvoker).invoke(any());
        defaultInvocationJob.doRun();
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.RUNNING);
        verify(mockExperimentContext).setAssumeState(ExperimentPartState.ERROR);
        verify(mockExperimentContext).setFailureState(ExperimentPartState.RUNNING);
        verify(mockExperimentContext).setFailureState(ExperimentPartState.FINISHED);
        verify(mockExperimentContext, never()).setAssumeRun(any(LocalDateTime.class));
        verify(mockExperimentContext, never()).setSuccessRun(any(LocalDateTime.class));
        verify(mockExperimentContext).setFailureRun(any(LocalDateTime.class));
        verify(mockExperimentContext).getAssume();
        verify(mockExperimentContext, never()).getSuccess();
        verify(mockExperimentContext, times(2)).getFailure();
        verify(mockMethodInvoker, times(2)).invoke(any(MethodDescriptor.class));
    }
}