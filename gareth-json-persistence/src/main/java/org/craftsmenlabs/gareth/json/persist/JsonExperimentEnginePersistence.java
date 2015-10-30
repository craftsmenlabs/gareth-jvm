package org.craftsmenlabs.gareth.json.persist;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hylke on 30/10/15.
 */
public class JsonExperimentEnginePersistence implements ExperimentEnginePersistence {

    @Override
    public void persist(final ExperimentEngine experimentEngine) throws GarethStateWriteException {
        final List<JsonExperimentContextData> data = new ArrayList<>();
        experimentEngine.getExperimentRunContexts().forEach((experimentRunContext -> {
            data.add(assembleToContextData(experimentRunContext));
        }));

        final ObjectMapper objectMapper = new ObjectMapper();
        final JaxbAnnotationModule module = new JaxbAnnotationModule();
        objectMapper.registerModule(module);
        final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        objectMapper.setAnnotationIntrospector(introspector);
    }

    @Override
    public void restore(final ExperimentEngine experimentEngine) throws GarethStateReadException {

    }


    private JsonExperimentContextData assembleToContextData(final ExperimentRunContext experimentRunContext) {
        JsonExperimentContextData jsonExperimentContextData = null;
        if (null != experimentRunContext) {
            jsonExperimentContextData = new JsonExperimentContextData();
            jsonExperimentContextData.setHash(experimentRunContext.getHash());
            // Set run data
            jsonExperimentContextData.setBaselineRun(experimentRunContext.getBaselineRun());
            jsonExperimentContextData.setAssumeRun(experimentRunContext.getAssumeRun());
            jsonExperimentContextData.setSuccessRun(experimentRunContext.getSuccessRun());
            jsonExperimentContextData.setFailureRun(experimentRunContext.getFailureRun());
            // Set state
            jsonExperimentContextData.setBaselineState(experimentRunContext.getBaselineState());
            jsonExperimentContextData.setAssumeState(experimentRunContext.getAssumeState());
            jsonExperimentContextData.setSuccessState(experimentRunContext.getSuccessState());
            jsonExperimentContextData.setFailureState(experimentRunContext.getFailureState());
        }
        return jsonExperimentContextData;
    }

    @Override
    public ExperimentStateChangeListener getExperimentStateChangeListener() {
        return null;
    }
}
