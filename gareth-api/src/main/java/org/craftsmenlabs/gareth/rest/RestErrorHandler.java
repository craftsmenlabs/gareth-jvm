package org.craftsmenlabs.gareth.rest;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringJoiner;

public class RestErrorHandler extends DefaultResponseErrorHandler {

    private boolean hasError = false;
    private String responseCodeTxt = null;
    private int responseCode;
    private String responseBody;

    public void handleError(ClientHttpResponse response) throws IOException {
        hasError = true;
        responseCodeTxt = response.getStatusText();
        responseCode = response.getRawStatusCode();
        Collection<String> strings = readLines(response.getBody());
        StringJoiner stringJoiner = new StringJoiner("");
        strings.forEach(s -> stringJoiner.add(s));
        responseBody = stringJoiner.toString();
    }

    public String getResponseCodeTxt() {
        return responseCodeTxt;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean hasError() {
        return hasError;
    }

    public String getResponseBody() {
        return responseBody;
    }

    private Collection<String> readLines(InputStream is) {
        LinkedList out = new LinkedList();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line;
            while ((line = r.readLine()) != null) {
                out.add(line);
            }
            return out;
        } catch (IOException ioe) {
            throw new RuntimeException("Problems reading from: " + is, ioe);
        }
    }

    public void reset() {
        hasError = false;
        responseCodeTxt = null;
        responseCode = 0;
        responseBody = null;
    }
}