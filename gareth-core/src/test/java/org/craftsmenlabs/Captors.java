package org.craftsmenlabs;

import mockit.Verifications;
import org.craftsmenlabs.gareth.ExperimentStorage;
import org.craftsmenlabs.gareth.model.Experiment;
import org.craftsmenlabs.gareth.monitors.ExperimentExecutor;

import java.util.ArrayList;
import java.util.List;

public class Captors {

    public static List<Experiment> experimentStorage_save(ExperimentStorage experimentStorage) {
        final List<Experiment> captor = new ArrayList<>();
        new Verifications() {{
            experimentStorage.updateExperiment(withCapture(captor));
        }};
        return captor;
    }

    public static List<Experiment> experimentExecutor_executeBaseline(ExperimentExecutor experimentExecutor) {
        final List<Experiment> captor = new ArrayList<>();
        new Verifications() {{
            experimentExecutor.executeBaseline(withCapture(captor));
        }};
        return captor;
    }

    public static List<Experiment> experimentExecutor_executeAssume(ExperimentExecutor experimentExecutor) {
        final List<Experiment> captor = new ArrayList<>();
        new Verifications() {{
            experimentExecutor.executeAssume(withCapture(captor));
        }};
        return captor;
    }
}