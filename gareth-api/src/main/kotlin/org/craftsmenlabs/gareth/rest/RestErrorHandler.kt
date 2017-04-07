package org.craftsmenlabs.gareth.rest

import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.DefaultResponseErrorHandler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*


class RestErrorHandler : DefaultResponseErrorHandler() {

    var hasError = false
        private set
    var responseCodeTxt: String? = null
        private set
    var responseCode: Int = 0
        private set
    var responseBody: String? = null
        private set

    override fun handleError(response: ClientHttpResponse) {
        hasError = true
        responseCodeTxt = response.statusText
        responseCode = response.rawStatusCode
        val strings = readLines(response.body)
        val stringJoiner = StringJoiner("")
        strings.forEach { s -> stringJoiner.add(s) }
        responseBody = stringJoiner.toString()
    }

    private fun readLines(inputStream: InputStream): List<String> {
        try {
            val r = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            return if (r != null) r.readLines() else listOf()
        } catch (ioe: IOException) {
            throw RuntimeException("Problems reading from: " + inputStream, ioe)
        }
    }

    fun reset() {
        hasError = false
        responseCodeTxt = null
        responseCode = 0
        responseBody = null
    }
}