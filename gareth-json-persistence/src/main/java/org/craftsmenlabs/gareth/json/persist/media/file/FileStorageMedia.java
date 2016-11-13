package org.craftsmenlabs.gareth.json.persist.media.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentContextData;
import org.craftsmenlabs.gareth.json.persist.media.AbstractStorageMedia;
import org.craftsmenlabs.gareth.json.persist.media.StorageMedia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class FileStorageMedia extends AbstractStorageMedia implements StorageMedia {

    private final static Logger LOG = LoggerFactory.getLogger(FileStorageMedia.class);

    private final File stateFile;

    private FileStorageMedia(final Builder builder) {
        this.stateFile = builder.stateFile;
    }

    @Override
    public void persist(final List<JsonExperimentContextData> jsonExperimentContextDataList) throws GarethStateWriteException {
        final ObjectMapper objectMapper = getObjectMapper();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(stateFile);
            objectMapper.writeValue(fos, jsonExperimentContextDataList);
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

    @Override
    public List<JsonExperimentContextData> restore() throws GarethStateReadException {
        List<JsonExperimentContextData> jsonExperimentContextDataList = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(stateFile);
            jsonExperimentContextDataList = getObjectMapper()
                    .readValue(fis, new TypeReference<List<JsonExperimentContextData>>() {
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


    public static class Builder {

        private final static String STATE_FILENAME = "gareth.state.json";
        private final static String TMP_DIR = System.getProperty("java.io.tmpdir");

        private File stateFile;

        public Builder setStateFile(final File stateFile) {
            this.stateFile = stateFile;
            return this;
        }

        public FileStorageMedia build() {
            setupStateFile();
            return new FileStorageMedia(this);
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
