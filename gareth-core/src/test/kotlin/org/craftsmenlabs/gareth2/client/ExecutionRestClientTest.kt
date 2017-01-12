package org.craftsmenlabs.gareth2.client

import mockit.Deencapsulation
import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.assertj.core.api.Assertions
import org.craftsmenlabs.gareth.api.model.DefinitionInfo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

@RunWith(JMockit::class)
class ExecutionRestClientTest {
    @Injectable
    lateinit var restTemplate: RestTemplate
    @Tested
    lateinit var restClient: ExecutionRestClient
    @Injectable
    lateinit var entity: ResponseEntity<DefinitionInfo>
    val status = HttpStatus.ACCEPTED
    @Injectable
    lateinit var info: DefinitionInfo


    fun setup() {
        Deencapsulation.setField(restClient, "host", "http://gareth.io/acme")
    }

    @Test
    fun testRequestWithValidGlueline() {
        setup()
        object : Expectations() {
            init {
                restTemplate.getForEntity("http://gareth.io/acme/gareth/v1/definitions/baseline/Sale of fruit", DefinitionInfo::class.java)
                result = entity
                entity.statusCode
                result = status
                entity.body
                result = info
                info.glueline
                result = "Sale of fruit"
            }
        }
        Assertions.assertThat(restClient.isValidGlueLine("baseline", "Sale of fruit")).isTrue()
    }
}