package org.craftsmenlabs.gareth.json.persist;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.json.persist.serializer.StorageSerializer;

import java.time.LocalDateTime;

@Data
public class JsonExperimentContextData {

    private String hash;
    private LocalDateTime baselineRun, assumeRun, successRun, failureRun;
    private ExperimentPartState baselineState, assumeState, successState, failureState;
    @JsonSerialize(using = StorageSerializer.class)
    private Storage storage;

}
