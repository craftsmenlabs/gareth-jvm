package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ExperimentRunner {
    private static Logger logger = LoggerFactory.getLogger(ExperimentRunner.class);
    private final MethodInvoker methodInvoker;
    private final AssumeScheduler assumeScheduler;
    private final boolean ignoreInvocationExceptions;

    @FunctionalInterface
    public interface Informer {
        void invoke();
    }

    public ExperimentRunner(final MethodInvoker methodInvoker,final AssumeScheduler assumeScheduler,final boolean ignoreInvocationExceptions) {
        this.methodInvoker = methodInvoker;
        this.assumeScheduler = assumeScheduler;
        this.ignoreInvocationExceptions = ignoreInvocationExceptions;
    }

    protected void invokeBaseline(final ExperimentRunContext runContext, final Informer onStateChange) {
        //final MethodDescriptor baselineMethodDescriptor = runContext.getExperimentContext().getBaseline();
        final String glueLineInExperiment = runContext.getExperimentContext().getBaselineGlueLine();
        logger.debug(String.format("Invoking baseline: %s with state %s", glueLineInExperiment, runContext
                .getBaselineState().getName()));
        if (ExperimentPartState.OPEN == runContext.getBaselineState()) {
            try {
                runContext.setBaselineState(ExperimentPartState.RUNNING);
                MethodDescriptor baseline = runContext.getExperimentContext().getBaseline();
                if (runContext.getExperimentContext().hasStorage()) {
                    methodInvoker.invoke(glueLineInExperiment, baseline, runContext.getStorage());
                } else {
                    methodInvoker.invoke(glueLineInExperiment, baseline);
                }
                runContext.setBaselineState(ExperimentPartState.FINISHED);
                runContext.setBaselineRun(LocalDateTime.now());
            } catch (final GarethUnknownDefinitionException | GarethInvocationException e) {
                runContext.setBaselineState(ExperimentPartState.ERROR);
                if (!ignoreInvocationExceptions) {
                    throw e;
                }
            }
            onStateChange.invoke();
        }
    }


    protected void scheduleInvokeAssume(final ExperimentRunContext experimentRunContext, final ExperimentEngine engine) {
        if (ExperimentPartState.OPEN == experimentRunContext.getAssumeState()) {
            try {
                assumeScheduler.schedule(experimentRunContext, engine);
            } catch (final GarethUnknownDefinitionException | GarethInvocationException e) {
                if (!ignoreInvocationExceptions) {
                    throw e;
                }
            }
        }
    }

}
