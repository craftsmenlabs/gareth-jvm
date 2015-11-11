package org.craftsmenlabs.gareth.json.persist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.craftsmenlabs.gareth.json.persist.listener.JsonExperimentChangeListener;
import org.craftsmenlabs.gareth.json.persist.serializer.StorageDeserializer;
import org.craftsmenlabs.gareth.json.persist.serializer.StorageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hylke on 30/10/15.
 */
public class JsonExperimentEnginePersistence implements ExperimentEnginePersistence {

    private static final Logger LOG = LoggerFactory.getLogger(JsonExperimentEnginePersistence.class);

    private final File stateFile;

    private final JsonExperimentChangeListener jsonExperimentChangeListener;

    private JsonExperimentEnginePersistence(final Builder builder) {
        this.stateFile = builder.stateFile;
        jsonExperimentChangeListener = new JsonExperimentChangeListener.Builder(this).build();
    }

    @Override
    public void persist(final ExperimentEngine experimentEngine) throws GarethStateWriteException {
        final List<JsonExperimentContextData> data = new ArrayList<>();
        experimentEngine.getExperimentRunContexts().forEach((experimentRunContext -> {
            data.add(assembleToContextData(experimentRunContext));
        }));

        final ObjectMapper objectMapper = getObjectMapper();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(stateFile);
            objectMapper.writeValue(fos, data);
        } catch (final FileNotFoundException e) {
            LOG.error("File cannot be found", e);
            throw new GarethStateWriteException(e);
        } catch (final IOException e) {
            LOG.error("Error while writing experiment engine data", e);
            throw new GarethStateWriteException(e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Storage.class, new StorageSerializer());
        simpleModule.addDeserializer(Storage.class, new StorageDeserializer());
        //Register modules
        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(new JavaTimeModule());
        final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(objectMapper.getTypeFactory());
        objectMapper.setAnnotationIntrospector(introspector);
        return objectMapper;
    }

    @Override
    public void restore(final ExperimentEngine experimentEngine) throws GarethStateReadException {
        final List<JsonExperimentContextData> experimentContextDataList = readExperimentContextDataFromFile();

        experimentContextDataList.forEach(experimentContextData -> {
            try {
                final ExperimentContext experimentContext = experimentEngine.findExperimentContextForHash(experimentContextData.getHash());
                experimentEngine.getExperimentRunContexts().add(rebuildExperimentRunContext(experimentContextData, experimentContext));
            } catch (final GarethUnknownExperimentException e) {
                LOG.debug("No experiment context data found.", e);
            }
        });
    }

    private ExperimentRunContext rebuildExperimentRunContext(final JsonExperimentContextData experimentContextData, final ExperimentContext experimentContext) {
        final ExperimentRunContext experimentRunContext = new ExperimentRunContextImpl
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

    private List<JsonExperimentContextData> readExperimentContextDataFromFile() throws GarethStateReadException {
        List<JsonExperimentContextData> jsonExperimentContextDataList = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(stateFile);
            jsonExperimentContextDataList = getObjectMapper().readValue(fis, new TypeReference<List<JsonExperimentContextData>>() {
            });
        } catch (final FileNotFoundException e) {
            LOG.error("File cannot be found", e);
            throw new GarethStateReadException(e);
        } catch (final IOException e) {
            LOG.error("Exception while reading file", e);
            throw new GarethStateReadException(e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return jsonExperimentContextDataList;
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

    public static class Builder {

        private final static String STATE_FILENAME = "gareth.state.json";
        private final static String TMP_DIR = System.getProperty("java.io.tmpdir");

        private File stateFile;

        public Builder setStateFile(final File stateFile) {
            this.stateFile = stateFile;
            return this;
        }

        public ExperimentEnginePersistence build() {
            setupStateFile();
            return new JsonExperimentEnginePersistence(this);
        }

        private void setupStateFile() {
            if (null == stateFile) {
                stateFile = new File(TMP_DIR, STATE_FILENAME);
            }
            try {
                if (!stateFile.exists()) {
                    stateFile.createNewFile();
                }
            } catch (final IOException e) {
                throw new IllegalStateException(String.format("Cannot setup state file %s", stateFile.getPath()), e);
            }
        }
    }
}
