package org.craftsmenlabs.gareth.execution

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.craftsmenlabs.gareth.validator.rest.ExecutionEndpointClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
@Profile("!test")
open class RestClientConfig {

    @Value("\${gareth.hub.url:}")
    private lateinit var url: String

    @Bean
    open fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()

        val javaTimeModule = JavaTimeModule()
        mapper.registerModule(javaTimeModule)

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        return mapper
    }


    @Bean
    open fun retrofitClient(): Retrofit.Builder {
        val builder = Retrofit.Builder()
        return builder.addConverterFactory(JacksonConverterFactory.create(objectMapper()))
    }

    @Bean
    open fun executionEndpointClient(retrofitBuilder: Retrofit.Builder): ExecutionEndpointClient {
        return retrofitBuilder.baseUrl(url).build().create(ExecutionEndpointClient::class.java!!)
    }


}