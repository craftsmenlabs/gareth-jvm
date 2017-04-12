package org.craftsmenlabs;

import mockit.Verifications;
import org.craftsmenlabs.gareth.monitors.ExperimentExecutor;
import org.craftsmenlabs.gareth.services.ExperimentService;
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO;

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

    public static List<ExperimentDTO> experimentExecutor_executeBaseline(ExperimentExecutor experimentExecutor) {
        final List<ExperimentDTO> captor = new ArrayList<>();
        new Verifications() {{
            experimentExecutor.executeBaseline(withCapture(captor));
        }};
        return captor;
    }

    public static List<ExperimentDTO> experimentExecutor_executeAssume(ExperimentExecutor experimentExecutor) {
        final List<ExperimentDTO> captor = new ArrayList<>();
        new Verifications() {{
            experimentExecutor.executeAssume(withCapture(captor));
        }};
        return captor;
    }
}