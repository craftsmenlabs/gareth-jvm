package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.core.util.ExperimentContextHashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ExperimentContextBuilder {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentContextBuilder.class);
    private final DefinitionRegistry definitionRegistry;
    private final ExperimentEngineConfig experimentEngineConfig;

    public ExperimentContextBuilder(final DefinitionRegistry definitionRegistry,
                                    final ExperimentEngineConfig experimentEngineConfig) {
        this.definitionRegistry = definitionRegistry;
        this.experimentEngineConfig = experimentEngineConfig;
    }

    protected ExperimentContext build(final Experiment experiment) {
        return build(Arrays.asList(experiment)).get(0);
    }

    protected List<ExperimentContext> build(final List<Experiment> experiments) {
        List<ExperimentContext> experimentContexts = new ArrayList<>();
        logger.info("Populating experiment contexts");
        for (final Experiment experiment : experiments) {
            for (final AssumptionBlock assumptionBlock : experiment.getAssumptionBlockList()) {

                final String[] surrogateKey = {experiment.getExperimentName()
                        , assumptionBlock.getBaseline()
                        , assumptionBlock.getAssumption()
                        , assumptionBlock.getTime()
                        , assumptionBlock.getSuccess()
                        , assumptionBlock.getFailure()};

                final String hashedSurrogateKey = ExperimentContextHashGenerator.generateHash(surrogateKey);

                final ExperimentContext experimentContext = new ExperimentContext
                        .Builder(experiment.getExperimentName(), assumptionBlock)
                        .setBaseline(Optional.ofNullable(getBaseline(assumptionBlock.getBaseline())))
                        .setAssume(Optional.ofNullable(getAssume(assumptionBlock.getAssumption())))
                        .setTime(getDuration(assumptionBlock.getTime()))
                        .setFailure(Optional.ofNullable(getFailure(assumptionBlock.getFailure())))
                        .setSuccess(Optional.ofNullable(getSuccess(assumptionBlock.getSuccess())))
                        .build(hashedSurrogateKey);

                experimentContexts.add(experimentContext);
            }
        }
        logger.info(String.format("Added %d different experiments", experimentContexts.size()));
        return experimentContexts;
    }

    private Duration getDuration(final String timeGlueLine) {
        Duration duration = null;
        try {
            duration = definitionRegistry.getDurationForTime(timeGlueLine);
        } catch (final GarethUnknownDefinitionException e) {
            throw new GarethInvocationException(e);
        }
        return duration;
    }

    private MethodDescriptor getSuccess(final String glueLine) {
        MethodDescriptor method = null;
        try {
            method = definitionRegistry.getMethodDescriptorForSuccess(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }

    private MethodDescriptor getAssume(final String glueLine) {
        MethodDescriptor method = null;
        try {
            method = definitionRegistry.getMethodDescriptorForAssume(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }


    private MethodDescriptor getBaseline(final String glueLine) {
        MethodDescriptor method = null;
        try {
            method = definitionRegistry.getMethodDescriptorForBaseline(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }

    private MethodDescriptor getFailure(final String glueLine) {
        MethodDescriptor method = null;
        try {
            method = definitionRegistry.getMethodDescriptorForFailure(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }

}
