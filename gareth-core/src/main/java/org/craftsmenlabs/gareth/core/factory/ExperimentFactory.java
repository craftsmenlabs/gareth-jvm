package org.craftsmenlabs.gareth.core.factory;

import org.antlr.v4.runtime.*;
import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.GarethLexer;
import org.craftsmenlabs.gareth.GarethParser;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.core.listener.ExperimentBaseListener;

import java.io.InputStream;

/**
 * Created by hylke on 04/08/15.
 */
public class ExperimentFactory {

    public Experiment buildExperiment(final InputStream inputStream) throws GarethExperimentParseException {
        try {
            final ANTLRInputStream antlrInputStream = new ANTLRInputStream(inputStream);

            final GarethLexer garethLexer = new GarethLexer(antlrInputStream);
            final GarethParser garethParser = new GarethParser(new CommonTokenStream(garethLexer));
            garethParser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(final Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
                }
            });


            final ExperimentBaseListener experimentListener = new ExperimentBaseListener();
            garethParser.addParseListener(experimentListener);
            garethParser.experimentBlock();


            return experimentListener.getExperiment();

        } catch (Exception e) {
            throw new GarethExperimentParseException();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
