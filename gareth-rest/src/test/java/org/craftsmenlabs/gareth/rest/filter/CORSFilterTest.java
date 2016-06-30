package org.craftsmenlabs.gareth.rest.filter;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CORSFilterTest {

    private CORSFilter corsFilter;

    @Mock
    private ContainerRequest mockContainerRequest;

    @Mock
    private ContainerResponse mockContainerResponse;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        corsFilter = new CORSFilter();
    }

    @Test
    public void testFilter() throws Exception {
        final MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
        when(mockContainerResponse.getHeaders()).thenReturn(headers);

        corsFilter.filter(mockContainerRequest, mockContainerResponse);

        assertThat(headers.containsKey("Access-Control-Allow-Origin")).isTrue();
        assertThat(headers.containsKey("Access-Control-Allow-Headers")).isTrue();
        assertThat(headers.containsKey("Access-Control-Allow-Credentials")).isTrue();
        assertThat(headers.containsKey("Access-Control-Allow-Methods")).isTrue();
        assertThat(headers.containsKey("Access-Control-Max-Age")).isTrue();
    }
}