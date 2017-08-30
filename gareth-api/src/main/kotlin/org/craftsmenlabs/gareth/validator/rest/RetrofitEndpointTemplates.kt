package org.craftsmenlabs.gareth.validator.rest

import org.craftsmenlabs.gareth.validator.model.*
import retrofit2.Call
import retrofit2.http.*

interface ExperimentTemplateEndpointClient {

    @PUT("gareth/validator/v1/templates")
    fun update(@Body dto: ExperimentTemplateUpdateDTO): Call<ExperimentTemplateDTO>

    @POST("gareth/validator/v1/templates")
    fun create(@Body dto: ExperimentTemplateCreateDTO): Call<ExperimentTemplateDTO>

    @GET("gareth/validator/v1/templates/{id}")
    fun get(@Path("id") id: Long): Call<ExperimentTemplateDTO>

    @GET("gareth/validator/v1/templates")
    fun getByName(@Query("name") name: String): Call<List<ExperimentTemplateDTO>>

}

interface OverviewEndpointClient {

    @GET("gareth/validator/v1/overview/{projectId}")
    fun getAllForProject(@Path("projectId") projectId: String): Call<List<OverviewDTO>>
}

interface ExperimentEndpointClient {

    @GET("gareth/validator/v1/experiments/{id}")
    fun get(@Path("id") id: String): Call<ExperimentDTO>

    @GET("gareth/validator/v1/experiments")
    fun getFiltered(@Query("created") ddMMYYYY: String?,
                    @Query("completed") completed: Boolean?,
                    @Query("status") status: ExecutionStatus?): Call<List<ExperimentDTO>>

    @POST("gareth/validator/v1/experiments")
    fun start(@Body dto: ExperimentCreateDTO): Call<ExperimentDTO>
}

interface GluelineLookupEndpointClient {
    @GET("gareth/validator/v1/glueline")
    fun lookupGlueline(@Query("type") glueLine: GlueLineType,
                       @Query("content") content: String): Call<GlueLineSearchResultDTO>
}

interface GarethHubClient {

    @GET(value = "gareth/validator/v1/execution/baselines/{id}")
    fun getBaselinesToExecute(@Path("id") id: String): List<ExecutionRequest>

    @GET(value = "gareth/validator/v1/execution/assumes/{id}")
    fun getAssumesToExecute(@Path("id") id: String): List<ExecutionRequest>

    @PUT(value = "gareth/validator/v1/execution/assumestatus/")
    fun updateAssumeStatus(result: AssumeExecutionResult)

    @PUT(value = "gareth/validator/v1/execution/baselinestatus/")
    fun updateBaselineStatus(result: BaselineExecutionResult)

    @PUT(value = "gareth/validator/v1/execution/registry/")
    fun updateRegistryForClient(@Body registry: DefinitionRegistryDTO)

}