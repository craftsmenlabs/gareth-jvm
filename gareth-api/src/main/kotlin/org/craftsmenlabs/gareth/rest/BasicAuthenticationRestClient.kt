package org.craftsmenlabs.gareth.rest

import org.apache.tomcat.util.codec.binary.Base64
import org.craftsmenlabs.BadRequestException
import org.craftsmenlabs.NotFoundException
import org.craftsmenlabs.RequestForbiddenException
import org.craftsmenlabs.RestException
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset
import java.util.*


class BasicAuthenticationRestClient(
        private val userName: String,
        private val password: String,
        private val errorHandler: RestErrorHandler = RestErrorHandler()) {
    private val LOGGER = LoggerFactory.getLogger(BasicAuthenticationRestClient::class.java)

    private val basicAuthenticationHeaders: HttpHeaders
    private val template: RestTemplate

    init {
        basicAuthenticationHeaders = createAuthenticationHeaders(userName, password)
        val messageConverter = MappingJackson2HttpMessageConverter()
        template = RestTemplate(Arrays.asList(messageConverter, FormHttpMessageConverter()))
    }

    operator fun <T : Any> get(cls: Class<T>, url: String): T {
        return getAsEntity(cls, url).body
    }

    fun <T : Any> getAsEntity(cls: Class<T>, url: String): ResponseEntity<T> {
        return exchange(HttpMethod.GET, url, Optional.empty<Any>(), cls)
    }

    fun <T : Any> putAsEntity(body: Any, cls: Class<T>, url: String): ResponseEntity<T> {
        return exchange(HttpMethod.PUT, url, Optional.of(body), cls)
    }

    fun <T : Any> put(body: Any, cls: Class<T>, url: String): T {
        return putAsEntity(body, cls, url).body
    }

    fun <T : Any> post(url: String, cls: Class<T>): ResponseEntity<T> {
        return exchange(HttpMethod.POST, url, Optional.empty<Any>(), cls)
    }

    fun <T : Any> post(body: Any, cls: Class<T>, url: String): T {
        return postAsEntity(body, cls, url).body
    }

    fun <T : Any> postAsEntity(body: Any, cls: Class<T>, url: String): ResponseEntity<T> {
        return exchange(HttpMethod.POST, url, Optional.of(body), cls)
    }

    fun delete(url: String) {
        exchange<Any>(HttpMethod.DELETE, url, Optional.empty<Any>(), null)
    }

    private fun <T> exchange(method: HttpMethod, url: String, body: Optional<Any>, cls: Class<T>?): ResponseEntity<T> {
        val methodPlusUrl = method.name + " " + url
        try {
            val requestHeaders = createHeadersForRequest(url)
            errorHandler.reset()
            LOGGER.debug("Executing {} (client:{}). with headers: {}.", methodPlusUrl, toString(), requestHeaders)
            if (body != null) {
                LOGGER.debug("body: {}.", body)
            }
            val responseEntity = template
                    .exchange(url, method, createEntity(requestHeaders, body), cls)
            if (errorHandler.hasError) {
                LOGGER.error(
                        "REST call to {} returned {} error. Message: {} ",
                        methodPlusUrl,
                        errorHandler.responseCodeTxt,
                        errorHandler.responseBody)
            } else {
                LOGGER.debug("Rest call to URL {} returned OK.", methodPlusUrl)
            }
            return responseEntity
        } catch (ex: HttpClientErrorException) {
            return ResponseEntity.status(ex.statusCode).body(ex.message) as ResponseEntity<T>
        } catch (ex: HttpServerErrorException) {
            return ResponseEntity.status(ex.statusCode).body(ex.message) as ResponseEntity<T>
        } catch (e: Exception) {
            return ResponseEntity.status(500).body(e.message) as ResponseEntity<T>
        }

    }

    private fun createHeadersForRequest(url: String): HttpHeaders {
        val requestHeaders = HttpHeaders()
        if (requestHeaders.isEmpty() && basicAuthenticationHeaders != null) {
            requestHeaders.putAll(basicAuthenticationHeaders)
        }
        return requestHeaders
    }

    private fun createRestException(url: String, cee: HttpClientErrorException): RestException {
        val statusCode = cee.statusCode
        val text = url + " failed. Response: " + cee.message
        when (statusCode) {
            HttpStatus.BAD_REQUEST -> return BadRequestException(text, cee)
            HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN -> return RequestForbiddenException(text, cee)
            HttpStatus.NOT_FOUND -> return NotFoundException(text, cee)
            else -> return RestException(text, cee)
        }
    }

    private fun <T> createEntity(requestHeaders: HttpHeaders, body: Optional<T>): HttpEntity<T> {
        if (basicAuthenticationHeaders == null) {
            throw IllegalStateException("No authentication information present: " +
                    "When using the get/put/post/delete methods without username and password arguments, you must provide them during "
                    + "construction")
        }
        return newEntity(body, requestHeaders)
    }

    private fun <T> newEntity(body: Optional<T>, headers: HttpHeaders): HttpEntity<T> {
        if (body.isPresent) {
            return HttpEntity<T>(body.get(), headers)
        } else {
            return HttpEntity(headers)
        }
    }

    private fun createAuthenticationHeaders(name: String, password: String): HttpHeaders {
        return object : HttpHeaders() {
            init {
                val auth = name + ":" + password
                val charset = Charset.forName("US-ASCII")
                val encodedAuth = Base64.encodeBase64(
                        auth.toByteArray(charset))
                val authHeader = "Basic " + String(encodedAuth, charset)
                set("Authorization", authHeader)
            }
        }
    }

}
