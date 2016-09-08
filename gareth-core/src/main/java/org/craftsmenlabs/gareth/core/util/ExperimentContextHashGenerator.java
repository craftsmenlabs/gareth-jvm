package org.craftsmenlabs.gareth.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

public class ExperimentContextHashGenerator {

    private final static Logger LOG = LoggerFactory.getLogger(ExperimentContextHashGenerator.class);


    public static String generateHash(final String[] unhashedSurrogateKey) {
        byte[] digest = {};
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();

            final String unhashedKey = buildUnhashedKey(unhashedSurrogateKey);
            md.update(unhashedKey.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            digest = md.digest();
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOG.error("Cannot generate hash for experiment context");
        }
        return String.format("%064x", new java.math.BigInteger(1, digest));
    }

    private static String buildUnhashedKey(final String[] unhashedSurrogateKey) {
        final StringBuilder stringBuilder = new StringBuilder("");
        Optional.ofNullable(unhashedSurrogateKey).ifPresent(key -> {
            Arrays.stream(unhashedSurrogateKey)
                    .map(e -> Optional.ofNullable(e).orElse("-"))
                    .forEach(e -> stringBuilder.append(e));
        });
        return stringBuilder.toString();
    }
}
