package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.validator.GarethInvocationException
import org.craftsmenlabs.gareth.validator.model.ExecutionRunContext

data class AssumptionInvocationResult(val context: ExecutionRunContext,
                                      val successful: Boolean? = null,
                                      val exception: GarethInvocationException? = null)

data class GluelineInvocationResult(val context: ExecutionRunContext,
                                    val result: Any? = null,
                                    val exception: GarethInvocationException? = null)
