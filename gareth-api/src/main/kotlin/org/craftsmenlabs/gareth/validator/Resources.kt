package org.craftsmenlabs.gareth.validator

import org.craftsmenlabs.gareth.validator.model.*
import org.springframework.web.bind.annotation.RequestBody

interface DefinitionsResource {

    fun getBaselineByGlueline(glueLine: String): DefinitionInfo

    fun getAssumeByGlueline(glueLine: String): DefinitionInfo

    fun getSuccessByGlueline(glueLine: String): DefinitionInfo

    fun getFailureByGlueline(glueLine: String): DefinitionInfo

    fun getDurationByGlueline(glueLine: String): Duration
}

interface ExecutionResource {
    fun executeBaseline(dto: ExecutionRequest): BaselineExecutionResult

    fun executeAssumption(dto: ExecutionRequest): AssumeExecutionResult

    fun getTime(@RequestBody dto: ExecutionRequest): Duration
}

interface GlueLineMatcherResource {

    fun getBaselineByGlueline(glueLine: String): GlueLineSearchResultDTO

    fun getAssumeByGlueline(glueLine: String): GlueLineSearchResultDTO

    fun getSuccessByGlueline(glueLine: String): GlueLineSearchResultDTO

    fun getFailureByGlueline(glueLine: String): GlueLineSearchResultDTO

    fun getDurationByGlueline(glueLine: String): GlueLineSearchResultDTO
}
