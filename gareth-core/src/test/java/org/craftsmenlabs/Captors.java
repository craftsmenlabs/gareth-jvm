package org.craftsmenlabs;

import mockit.Verifications;
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO;
import org.craftsmenlabs.gareth.validator.services.ExperimentService;

import java.util.ArrayList;
import java.util.List;

public class Captors {

    public static List<ExperimentDTO> experimentStorage_save(ExperimentService experimentStorage) {
        final List<ExperimentDTO> captor = new ArrayList<>();
        new Verifications() {{
            experimentStorage.updateExperiment(withCapture(captor));
        }};
        return captor;
    }

}