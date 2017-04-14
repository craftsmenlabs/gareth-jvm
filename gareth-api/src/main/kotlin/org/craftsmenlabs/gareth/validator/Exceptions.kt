package org.craftsmenlabs.gareth.validator

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

open class GarethException(msg: String? = null, cause: Throwable? = null) : RuntimeException(msg, cause)

class GarethIllegalDefinitionException(msg: String? = null, cause: Throwable? = null) : GarethException(msg, cause)

class GarethInvocationException(msg: String? = null, cause: Throwable? = null) : GarethException(msg, cause)

class GarethUnknownDefinitionException(msg: String) : GarethException(msg)

class NotFoundException(msg: String, cause: Throwable? = null) : RestException(msg, cause)

class BadRequestException(msg: String, cause: Throwable? = null) : RestException(msg, cause)

class RequestForbiddenException(msg: String, cause: Throwable? = null) : RestException(msg, cause)

open class RestException(msg: String, cause: Throwable?) : RuntimeException(msg, cause)

data class RestError @JsonCreator constructor(@JsonProperty(value = "status", required = true) val status: Int,
                                              @JsonProperty(value = "message", required = true) val message: String)