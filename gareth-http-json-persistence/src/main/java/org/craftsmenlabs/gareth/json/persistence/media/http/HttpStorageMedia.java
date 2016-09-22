package org.craftsmenlabs.gareth.json.persistence.media.http;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicHeader;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentContextData;
import org.craftsmenlabs.gareth.json.persist.media.AbstractStorageMedia;
import org.craftsmenlabs.gareth.json.persist.media.StorageMedia;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class HttpStorageMedia extends AbstractStorageMedia implements StorageMedia {

    private static final Header HEADER_CONTENT_TYPE = new BasicHeader("Content-Type", "application/json");

    private final String persistUrl;
    private final String restoreUrl;

    public HttpStorageMedia(String persistUrl, String restoreUrl) {
        this.persistUrl = persistUrl;
        this.restoreUrl = restoreUrl;
    }

    @Override
    public void persist(final List<JsonExperimentContextData> jsonExperimentContextDataList) throws GarethStateWriteException {
        try {
            final StringWriter outputWriter = new StringWriter();
            getObjectMapper().writeValue(outputWriter, jsonExperimentContextDataList);
            final Response response = Request.Post(persistUrl).addHeader(HEADER_CONTENT_TYPE)
                    .bodyByteArray(outputWriter.toString().getBytes()).execute();
            final int statusCode = response.returnResponse().getStatusLine().getStatusCode();
            if (!isOkStatusCode(statusCode)) {
                throw new GarethStateWriteException("Not a OK status code received");
            }
        } catch (final IOException e) {
            throw new GarethStateWriteException(e);
        }
    }

    private boolean isOkStatusCode(int statusCode) {
        return (statusCode >= 200 && statusCode < 300);
    }

    @Override
    public List<JsonExperimentContextData> restore() throws GarethStateReadException {
        List<JsonExperimentContextData> data = null;
        try {
            final HttpResponse response = Request.Get(restoreUrl).execute().returnResponse();
            final int statusCode = response.getStatusLine().getStatusCode();
            data = getObjectMapper()
                    .readValue(response.getEntity().getContent(), new TypeReference<List<JsonExperimentContextData>>() {
                    });
            if (!isOkStatusCode(statusCode)) {
                throw new GarethStateReadException("Not a OK status code received");
            }

        } catch (final IOException e) {
            throw new GarethStateReadException(e);
        }
        return data;
    }
}
