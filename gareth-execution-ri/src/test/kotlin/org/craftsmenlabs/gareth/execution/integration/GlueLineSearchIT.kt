package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.api.execution.GlueLineSearchResult
import org.craftsmenlabs.gareth.execution.Application
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("Test")
class GlueLineSearchIT {

    val path = "http://localhost:8090/gareth/v1/search/"
    val template = RestTemplate()

    @Test
    fun testBaseline() {
        assertThat(get("${path}baseline", "sale of fruit").suggestions).containsExactly("sale of *")
    }

    @Test
    fun testAssume() {
        assertThat(get("${path}assume", "sale of fruit has risen").suggestions).containsExactly("sale of fruit has risen by * per cent")
        assertThat(get("${path}assume", "sale of fruit has risen").exact).isNull()

        assertThat(get("${path}assume", "sale of fruit has risen by 20 per cent").exact).isEqualTo("sale of fruit has risen by * per cent")
    }

    @Test
    fun testDuration() {
        assertThat(get("${path}time", "1 week").exact).isEqualTo("* weeks?")
    }

    @Test
    fun testSuccess() {
        assertThat(get("${path}success", "send email to John").exact).isEqualTo("send email to *")
    }

    @Test
    fun testFailure() {
        assertThat(get("${path}failure", "send email to John").exact).isEqualTo("send email to *")
        assertThat(get("${path}failure", "send").suggestions).contains("send email to *", "send text to *")
    }

    fun get(path: String, glueLine: String): GlueLineSearchResult {
        val response = template.getForEntity(path + "/" + glueLine, GlueLineSearchResult::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        return response.body
    }

}

