package org.craftsmenlabs.gareth.core.util;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Created by hylke on 23/09/15.
 */
public class ExperimentContextHashGenerator {

    private final static Logger LOG = LoggerFactory.getLogger(ExperimentContextHashGenerator.class);

    public static String generateHash(final ExperimentContext experimentContext) {
        byte[] digest = {};
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(buildExperimentContextHash(experimentContext).getBytes("UTF-8")); // Change this to "UTF-16" if needed
            digest = md.digest();
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOG.error("Cannot generate hash for experiment context");
        }
        return String.format("%064x", new java.math.BigInteger(1, digest));
    }

    private static String buildExperimentContextHash(final ExperimentContext experimentContext) {
        final StringBuilder stringBuilder = new StringBuilder("");
        Optional.ofNullable(experimentContext).ifPresent((ec) -> {
            stringBuilder.append(ec.getExperimentName());
            stringBuilder.append(ec.getBaselineGlueLine());
            stringBuilder.append(ec.getAssumeGlueLine());
            stringBuilder.append(ec.getTimeGlueLine());
            stringBuilder.append(ec.getSuccessGlueLine());
            stringBuilder.append(ec.getFailureGlueLine());
        });
        return stringBuilder.toString();
    }
}
