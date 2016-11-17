package org.craftsmenlabs.gareth.execution.rest

import org.craftsmenlabs.gareth.api.exception.GarethInvocationException
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException
import org.craftsmenlabs.gareth.execution.NotFoundException
import org.craftsmenlabs.gareth.execution.dto.RestError
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(annotations = arrayOf(RestController::class))
class GlobalControllerAdvice {
    private val LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)


    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    fun resourceNotFoundHandler(req: HttpServletRequest, e: NotFoundException): RestError {
        return RestError(HttpStatus.NOT_FOUND.value(), e.message ?: "Unknown error")
    }

    @ExceptionHandler(GarethUnknownDefinitionException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun definitionError(req: HttpServletRequest, e: GarethUnknownDefinitionException): RestError {
        return RestError(HttpStatus.BAD_REQUEST.value(), e.message ?: "Unknown error")
    }

    @ExceptionHandler(GarethInvocationException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun invocationException(req: HttpServletRequest, e: GarethInvocationException): RestError {
        return RestError(HttpStatus.BAD_REQUEST.value(), e.message ?: "Unknown error")
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun genericException(req: HttpServletRequest, e: Exception): RestError {
        return RestError(501, "Unexpected error: " + e.message)
    }
}