package org.craftsmenlabs.gareth.json.persist.media;

import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentContextData;

import java.util.List;

/**
 * Created by hylke on 01/12/15.
 */
public interface StorageMedia {

    void persist(final List<JsonExperimentContextData> jsonExperimentContextDataList) throws GarethStateWriteException;

    List<JsonExperimentContextData> restore() throws GarethStateReadException;
}
