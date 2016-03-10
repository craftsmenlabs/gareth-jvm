package org.craftsmenlabs.gareth.json.persistence.media.http;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.http.client.utils.URIBuilder;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentContextData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by hylke on 15/01/16.
 */
public class HttpStorageMediaTest {

    private ClientAndServer mockServer;
    private int portNumber;

    private HttpStorageMedia httpStorageMedia;
    private URIBuilder persistURIBuilder, restoreURIBuilder;

    @Before
    public void before() throws Exception {
        portNumber = new Random().nextInt(30000 - 20000) + 20000;
        mockServer = ClientAndServer.startClientAndServer(portNumber);

        persistURIBuilder = new URIBuilder().setScheme("http").setHost("localhost").setPort(portNumber);
        restoreURIBuilder = new URIBuilder().setScheme("http").setHost("localhost").setPort(portNumber);
    }

    @After
    public void after() {
        mockServer.stop();
    }

    @Test
    public void testPersist() throws Exception {
        preparePersistWithStatusCode(200);
        httpStorageMedia.persist(new ArrayList<>());
        mockServer.verify(HttpRequest.request().withHeader("Content-Type", "application/json").withBody("[]"));
    }

    @Test
    public void testPersistWithCreatedStatusCode() throws Exception {
        preparePersistWithStatusCode(201);
        httpStorageMedia.persist(new ArrayList<>());
        mockServer.verify(HttpRequest.request().withHeader("Content-Type", "application/json").withBody("[]"));
    }

    @Test
    public void testPersistWithNotFoundStatusCode() throws Exception {
        validateInvalidPersistRequest(404);
    }


    @Test
    public void testPersistWithBadRequestStatusCode() throws Exception {
        validateInvalidPersistRequest(400);
    }

    @Test
    public void testPersistWithInternalServerErrorStatusCode() throws Exception {
        validateInvalidPersistRequest(500);
    }

    private void validateInvalidPersistRequest(final int statusCode) throws URISyntaxException {
        try {
            preparePersistWithStatusCode(statusCode);
            httpStorageMedia.persist(new ArrayList<>());
            fail("should not reach this point");
        } catch (final GarethStateWriteException e) {
            assertTrue(e.getMessage().contains("Not a OK status code received"));
        }
    }

    @Test
    public void testRestore() throws Exception {
        prepareRestoreWithStatusCode(200);
        final List<JsonExperimentContextData> jsonExperimentContextDataList = httpStorageMedia.restore();
        assertNotNull(jsonExperimentContextDataList);
        assertEquals(0, jsonExperimentContextDataList.size());
    }

    @Test
    public void testRestoreWithIncorrectBody() throws Exception {
        try {
            prepareRestoreWithStatusCode(200, "Dfsa");
            httpStorageMedia.restore();
        } catch (final GarethStateReadException e) {
            assertTrue(e.getCause() instanceof JsonParseException);
        }
    }

    @Test
    public void testRestoreNotFoundStatusCode() throws Exception {
        validateInvalidRestoreWithStatusCode(404);
    }

    @Test
    public void testRestoreBadRequestStatusCode() throws Exception {
        validateInvalidRestoreWithStatusCode(400);
    }

    @Test
    public void testRestoreInternalServerErrorStatusCode() throws Exception {
        validateInvalidRestoreWithStatusCode(500);
    }

    private void validateInvalidRestoreWithStatusCode(final int statusCode) throws URISyntaxException {
        try {
            prepareRestoreWithStatusCode(statusCode);
            httpStorageMedia.restore();
        } catch (final GarethStateReadException e) {
            assertTrue(e.getMessage().contains("Not a OK status code received"));
        }
    }

    public void buildHttpStorageMedia(final String persistUrl, final String restoreUrl) {
        httpStorageMedia = new HttpStorageMedia.Builder().setPeristUrl(persistUrl).setRestoreUrl(restoreUrl).build();
    }

    private void preparePersistWithStatusCode(final int statusCode) throws URISyntaxException {
        buildHttpStorageMedia(persistURIBuilder.setPath("/persist").build().toString(), null);
        mockServer.when(HttpRequest.request().withMethod("POST").withPath("/persist")).respond(HttpResponse.response().withStatusCode(statusCode));
    }

    private void prepareRestoreWithStatusCode(final int statusCode) throws URISyntaxException {
        prepareRestoreWithStatusCode(statusCode, "[]");
    }

    private void prepareRestoreWithStatusCode(final int statusCode, final String body) throws URISyntaxException {
        buildHttpStorageMedia(null, restoreURIBuilder.setPath("/restore").build().toString());
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/restore")).respond(HttpResponse.response().withStatusCode(statusCode).withBody(body));
    }
}