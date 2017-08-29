package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.validator.GarethInvocationException
import org.craftsmenlabs.gareth.validator.model.RunContext

data class AssumptionInvocationResult(val context: RunContext,
                                      val successful: Boolean? = null,
                                      val exception: GarethInvocationException? = null)

data class GluelineInvocationResult(val context: RunContext,
                                    val result: Any? = null,
                                    val exception: GarethInvocationException? = null)
