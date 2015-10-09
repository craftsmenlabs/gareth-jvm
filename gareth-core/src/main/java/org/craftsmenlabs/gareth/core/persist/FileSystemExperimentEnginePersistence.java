package org.craftsmenlabs.gareth.core.persist;

import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.util.ExperimentContextHashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hylke on 23/09/15.
 */
public class FileSystemExperimentEnginePersistence implements ExperimentEnginePersistence {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemExperimentEnginePersistence.class);

    private final File stateFile;

    private FileSystemExperimentEnginePersistence(final Builder builder) {
        this.stateFile = builder.stateFile;
    }

    @Override
    public void persist(final ExperimentEngine experimentEngine) throws GarethStateWriteException {
        final List<ExperimentContextData> data = new ArrayList<>();
        experimentEngine.getExperimentContexts().forEach((experimentContext -> {
            data.add(buildExperimentContextData(experimentContext));
        }));

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(stateFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
        } catch (final FileNotFoundException e) {
            LOG.error("File cannot be found", e);
            throw new GarethStateWriteException(e);
        } catch (final IOException e) {
            LOG.error("Error while writing experiment engine data", e);
            throw new GarethStateWriteException(e);
        } finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(fos);
        }

    }

    @Override
    public void restore(final ExperimentEngine experimentEngine) throws GarethStateReadException {
        final List<ExperimentContextData> experimentContextDataList = readExperimentContextDataFromFile();
        experimentEngine.getExperimentContexts().forEach(experimentContext -> {
            try {
                final ExperimentContextData experimentContextData = findExperimentContextDataForHash(experimentContextDataList, experimentContext.getHash());
                writeExperimentContextState(experimentContext, experimentContextData);

            } catch (final UnknownExperimentContextException e) {
                LOG.debug("No experiment context data found.", e);
            }
        });
    }

    private void writeExperimentContextState(final ExperimentContext experimentContext, final ExperimentContextData experimentContextData) {
        // Write states
        experimentContext.setBaselineState(experimentContextData.getBaselineState());
        experimentContext.setAssumeState(experimentContextData.getAssumeState());
        experimentContext.setSuccessState(experimentContextData.getSuccessState());
        experimentContext.setFailureState(experimentContext.getFailureState());
        // Write runs
        experimentContext.setBaselineRun(experimentContextData.getBaselineRun());
        experimentContext.setAssumeRun(experimentContextData.getAssumeRun());
        experimentContext.setSuccessRun(experimentContextData.getSuccessRun());
        experimentContext.setFailureRun(experimentContextData.getFailureRun());
        // Write storage
    }

    private ExperimentContextData findExperimentContextDataForHash(final List<ExperimentContextData> experimentContexts, final String hash) {
        return experimentContexts.parallelStream().filter(experimentContextData -> {
            return experimentContextData.getHash().equals(hash);
        })
                .findFirst()
                .orElseThrow(() -> new UnknownExperimentContextException(String.format("Cannot find experiment context data with hash %s", hash)));
    }

    private List<ExperimentContextData> readExperimentContextDataFromFile() throws GarethStateReadException {
        List<ExperimentContextData> experimentContextDataList = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(stateFile);
            ois = new ObjectInputStream(fis);
            experimentContextDataList = (List<ExperimentContextData>) ois.readObject();
        } catch (final ClassNotFoundException e) {
            LOG.error("Class cannot be found", e);
            throw new GarethStateReadException(e);
        } catch (final FileNotFoundException e) {
            LOG.error("File cannot be found", e);
            throw new GarethStateReadException(e);
        } catch (final IOException e) {
            LOG.error("Exception while reading file", e);
            throw new GarethStateReadException(e);
        } finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(fis);
        }
        return experimentContextDataList;
    }

    private ExperimentContextData buildExperimentContextData(final ExperimentContext experimentContext) {
        final ExperimentContextData experimentEngineContextData = new ExperimentContextData();
        // Set hash
        experimentEngineContextData.setHash(experimentContext.getHash());
        // Set run data
        experimentEngineContextData.setBaselineRun(experimentContext.getBaselineRun());
        experimentEngineContextData.setAssumeRun(experimentContext.getAssumeRun());
        experimentEngineContextData.setSuccessRun(experimentContext.getSuccessRun());
        experimentEngineContextData.setFailureRun(experimentContext.getFailureRun());
        // Set state
        experimentEngineContextData.setBaselineState(experimentContext.getBaselineState());
        experimentEngineContextData.setAssumeState(experimentContext.getAssumeState());
        experimentEngineContextData.setSuccessState(experimentContext.getSuccessState());
        experimentEngineContextData.setFailureState(experimentContext.getFailureState());
        // Set storage
        experimentEngineContextData.setStorage(experimentContext.getStorage());

        return experimentEngineContextData;
    }

    public static class Builder {

        private final static String STATE_FILENAME = "gareth.state";
        private final static String TMP_DIR = System.getProperty("java.io.tmpdir");

        private File stateFile;

        public Builder setStateFile(final File stateFile) {
            this.stateFile = stateFile;
            return this;
        }

        public ExperimentEnginePersistence build() {
            setupStateFile();
            return new FileSystemExperimentEnginePersistence(this);
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
