package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.model.*
import retrofit2.Call
import retrofit2.http.*

interface ExperimentTemplateEndpointClient {

    @PUT("gareth/v1/templates")
    fun update(@Body dto: ExperimentTemplateUpdateDTO): Call<ExperimentTemplateDTO>

    @POST("gareth/v1/templates")
    fun create(@Body dto: ExperimentTemplateCreateDTO): Call<ExperimentTemplateDTO>

    @GET("gareth/v1/templates/{id}")
    fun get(@Path("id") id: Long): Call<ExperimentTemplateDTO>
}

interface OverviewEndpointClient {

    @GET("gareth/v1/stats")
    fun getAll(): Call<List<OverviewDTO>>
}

interface ExperimentEndpointClient {

    @GET("gareth/v1/experiments/{id}")
    fun get(@Path("id") id: Long): Call<ExperimentDTO>

    @GET("gareth/v1/experiments")
    fun getFiltered(@Query("created") ddMMYYYY: String?,
                    @Query("completed") completed: Boolean?): Call<List<ExperimentDTO>>

    @POST("gareth/v1/experiments")
    fun start(@Body dto: ExperimentCreateDTO): Call<ExperimentDTO>
}

interface GluelineLookupEndpointClient {
    @GET("gareth/v1/glueline")
    fun lookupGlueline(@Query("type") glueLine: GlueLineType,
                       @Query("content") content: String): Call<GlueLineSearchResultDTO>
}
