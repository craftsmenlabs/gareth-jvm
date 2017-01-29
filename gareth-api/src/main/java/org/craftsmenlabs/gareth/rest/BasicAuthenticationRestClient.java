package org.craftsmenlabs.gareth.rest;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Convenience wrapper around Spring RestTemplate for basic authentication
 */
public class BasicAuthenticationRestClient
{
	Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationRestClient.class);

	private RestTemplate template;

	private String customName;

	private RestErrorHandler errorHandler;

	private HttpHeaders basicAuthenticationHeaders;

	BasicAuthenticationRestClient(
		RestTemplate template,
		String userName,
		String password,
		RestErrorHandler errorHandler)
	{
		this.template = template;
		this.errorHandler = errorHandler;
		setCredentials(userName, password);
	}

	public BasicAuthenticationRestClient()
	{
		setErrorHandler(new RestErrorHandler());
	}

	public BasicAuthenticationRestClient(String userName, String password)
	{
		setCredentials(userName, password);
	}

	public void setCredentials(String userName, String password)
	{
		LOGGER.info("trust store: {}", System.getProperty("javax.net.ssl.trustStore"));
		LOGGER.info("trust store password: {}", System.getProperty("javax.net.ssl.trustStorePassword"));
		basicAuthenticationHeaders = createAuthenticationHeaders(userName, password);
	}

	public <T extends Object> T get(Class<T> cls, String url)
	{
		return getAsEntity(cls, url).getBody();
	}

	public <T extends Object> ResponseEntity<T> getAsEntity(Class<T> cls, String url)
	{
		return exchange(HttpMethod.GET, url, Optional.empty(), cls);
	}

	public <T extends Object> ResponseEntity<T> putAsEntity(Object body, Class<T> cls, String url)
	{
		return exchange(HttpMethod.PUT, url, Optional.of(body), cls);
	}

	public <T extends Object> T put(Object body, Class<T> cls, String url)
	{
		return putAsEntity(body, cls, url).getBody();
	}

	public <T extends Object> ResponseEntity<T> post(String url, Class<T> cls)
	{
		return exchange(HttpMethod.POST, url, Optional.empty(), cls);
	}

	public <T extends Object> T post(Object body, Class<T> cls, String url)
	{
		return postAsEntity(body, cls, url).getBody();
	}

	public <T extends Object> ResponseEntity<T> postAsEntity(Object body, Class<T> cls, String url)
	{
		return exchange(HttpMethod.POST, url, Optional.of(body), cls);
	}

	public void delete(String url)
	{
		exchange(HttpMethod.DELETE, url, Optional.empty(), null);
	}

	public Optional<Integer> getErrorCode()
	{
		return getErrorHandler().hasError() ? Optional.of(getErrorHandler().getResponseCode()) : Optional.empty();
	}

	private <T> ResponseEntity<T> exchange(HttpMethod method, String url, Optional<Object> body, Class<T> cls)
	{
		String methodPlusUrl = method.name() + " " + url;
		try
		{
			//validateCredentialsForPost(method, url);
			HttpHeaders requestHeaders = createHeadersForRequest(url);
			getErrorHandler().reset();
			LOGGER.debug("Executing {} (client:{}). with headers: {}.", methodPlusUrl, toString(), requestHeaders);
			ResponseEntity<T> responseEntity = getTemplate()
				.exchange(url, method, createEntity(requestHeaders, body), cls);
			if (getErrorHandler().hasError())
			{
				LOGGER.error(
					"REST call to {} returned {} error. Message: {} ",
					methodPlusUrl,
					getErrorHandler().getResponseCodeTxt(),
					getErrorHandler().getResponseBody());
			}
			else
			{
				LOGGER.debug("Rest call to URL {} returned OK.", methodPlusUrl);
			}
			return responseEntity;
		}
		catch (HttpClientErrorException ex)
		{
			throw createRestException(methodPlusUrl, ex);
		}
		catch (Exception e)
		{
			throw new RestException(e, methodPlusUrl + " failed. Response: " + e.getMessage());
		}
	}

	private HttpHeaders createHeadersForRequest(String url)
	{
		HttpHeaders requestHeaders = new HttpHeaders();
		//If the headers already contain a JSESSIONID cookie, don't add the basic auth headers
		if (requestHeaders.isEmpty() && basicAuthenticationHeaders != null)
		{
			requestHeaders.putAll(basicAuthenticationHeaders);
		}
		return requestHeaders;
	}

	private RestException createRestException(String url, HttpClientErrorException cee)
	{
		HttpStatus statusCode = cee.getStatusCode();
		String text = url + " failed. Response: " + cee.getMessage();
		switch (statusCode)
		{
		case BAD_REQUEST:
			return new BadRequestException(text, cee);
		case UNAUTHORIZED:
		case FORBIDDEN:
			return new RequestForbiddenException(text, cee);
		case NOT_FOUND:
			return new NotFoundException(text, cee);
		default:
			return new RestException(cee, text);
		}
	}

	private RestTemplate getTemplate()
	{
		if (template == null)
		{
			MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
			template = new RestTemplate(Arrays.asList(messageConverter, new FormHttpMessageConverter()));
		}
		return template;
	}

	private <T> HttpEntity<T> createEntity(HttpHeaders requestHeaders, Optional<T> body)
	{
		if (basicAuthenticationHeaders == null)
		{
			throw new IllegalStateException("No authentication information present: " +
				"When using the get/put/post/delete methods without username and password arguments, you must provide them during "
                + "construction");
		}
		return newEntity(body, requestHeaders);
	}

	private <T> HttpEntity<T> newEntity(Optional<T> body, HttpHeaders headers)
	{
		if (body.isPresent())
		{
			return new HttpEntity<>(body.get(), headers);
		}
		else
		{
			return new HttpEntity<>(headers);
		}
	}

	private HttpHeaders createAuthenticationHeaders(String name, String password)
	{
		return new HttpHeaders()
		{
			{
				String auth = name + ":" + password;
				Charset charset = Charset.forName("US-ASCII");
				byte[] encodedAuth = Base64.encodeBase64(
					auth.getBytes(charset));
				String authHeader = "Basic " + new String(encodedAuth, charset);
				set("Authorization", authHeader);
			}
		};
	}

	public void setErrorHandler(RestErrorHandler handler)
	{
		this.errorHandler = handler;
		getTemplate().setErrorHandler(handler);
	}

	public RestErrorHandler getErrorHandler()
	{
		if (errorHandler == null)
		{
			setErrorHandler(new RestErrorHandler());
		}
		return errorHandler;
	}

}
