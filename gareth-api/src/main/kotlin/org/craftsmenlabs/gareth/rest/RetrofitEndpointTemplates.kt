package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.model.*
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

    @GET("gareth/validator/v1/templates")
    fun getAll(): Call<List<ExperimentTemplateDTO>>
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
                    @Query("completed") completed: Boolean?): Call<List<ExperimentDTO>>

    @POST("gareth/validator/v1/experiments")
    fun start(@Body dto: ExperimentCreateDTO): Call<ExperimentDTO>
}

interface GluelineLookupEndpointClient {
    @GET("gareth/validator/v1/glueline")
    fun lookupGlueline(@Query("type") glueLine: GlueLineType,
                       @Query("content") content: String): Call<GlueLineSearchResultDTO>
}
