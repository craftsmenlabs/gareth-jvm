package org.craftsmenlabs

open class GarethException : RuntimeException {
    constructor(msg: String) : super(msg)
    constructor(cause: Throwable) : super(cause)
}

class GarethIllegalDefinitionException : GarethException {
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String) : super(msg)
}

class GarethInvocationException(cause: Throwable) : GarethException(cause)

class GarethUnknownDefinitionException(msg: String) : GarethException(msg)

class NotFoundException(msg: String, cause: Throwable? = null) : RestException(msg, cause)

class BadRequestException(msg: String, cause: Throwable? = null) : RestException(msg, cause)

class RequestForbiddenException(msg: String, cause: Throwable? = null) : RestException(msg, cause)

open class RestException(msg: String, cause: Throwable?) : RuntimeException(msg, cause)