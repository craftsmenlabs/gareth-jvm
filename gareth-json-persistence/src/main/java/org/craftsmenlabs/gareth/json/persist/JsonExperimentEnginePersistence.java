package org.craftsmenlabs.gareth.json.persist;

import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.core.persist.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.core.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.json.persist.listener.JsonExperimentChangeListener;
import org.craftsmenlabs.gareth.json.persist.media.StorageMedia;
import org.craftsmenlabs.gareth.json.persist.media.file.FileStorageMedia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JsonExperimentEnginePersistence implements ExperimentEnginePersistence {

    private static final Logger LOG = LoggerFactory.getLogger(JsonExperimentEnginePersistence.class);

    private final StorageMedia storageMedia;

    private final JsonExperimentChangeListener jsonExperimentChangeListener;

    public JsonExperimentEnginePersistence() {
        this(new FileStorageMedia.Builder().build());
    }

    public JsonExperimentEnginePersistence(StorageMedia storageMedia) {
        this.storageMedia = storageMedia;
        jsonExperimentChangeListener = new JsonExperimentChangeListener(this);
    }

    @Override
    public void persist(final ExperimentEngine experimentEngine) throws GarethStateWriteException {
        final List<JsonExperimentContextData> data = new ArrayList<>();
        experimentEngine.getExperimentRunContexts().forEach((experimentRunContext -> {
            data.add(assembleToContextData(experimentRunContext));
        }));

        storageMedia.persist(data);
    }

    @Override
    public void restore(final ExperimentEngine experimentEngine) throws GarethStateReadException {
        final List<JsonExperimentContextData> experimentContextDataList = storageMedia.restore();

        experimentContextDataList.forEach(experimentContextData -> {
            try {
                final ExperimentContext experimentContext = experimentEngine
                        .findExperimentContextForHash(experimentContextData.getHash());
                experimentEngine.getExperimentRunContexts()
                        .add(rebuildExperimentRunContext(experimentContextData, experimentContext));
            } catch (final GarethUnknownExperimentException e) {
                LOG.debug("No experiment context data found.", e);
            }
        });
    }

    private ExperimentRunContext rebuildExperimentRunContext(final JsonExperimentContextData experimentContextData, final ExperimentContext experimentContext) {
        final ExperimentRunContext experimentRunContext = new ExperimentRunContext
                .Builder(experimentContext, experimentContextData.getStorage())
                .build();
        experimentRunContext.setBaselineState(experimentContextData.getBaselineState());
        experimentRunContext.setAssumeState(experimentContextData.getAssumeState());
        experimentRunContext.setSuccessState(experimentContextData.getSuccessState());
        experimentRunContext.setFailureState(experimentContextData.getFailureState());
        // Write runs
        experimentRunContext.setBaselineRun(experimentContextData.getBaselineRun());
        experimentRunContext.setAssumeRun(experimentContextData.getAssumeRun());
        experimentRunContext.setSuccessRun(experimentContextData.getSuccessRun());
        experimentRunContext.setFailureRun(experimentContextData.getFailureRun());

        return experimentRunContext;
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
            // Storage
            jsonExperimentContextData.setStorage(experimentRunContext.getStorage());
        }
        return jsonExperimentContextData;
    }

    @Override
    public ExperimentStateChangeListener getExperimentStateChangeListener() {
        return jsonExperimentChangeListener;
    }
}
