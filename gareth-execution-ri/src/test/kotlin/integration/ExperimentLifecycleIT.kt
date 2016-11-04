package integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.Application
import org.craftsmenlabs.gareth.execution.dto.GlueLineDTO
import org.craftsmenlabs.gareth.execution.rest.ExecutionEndPoint
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class), webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ExperimentLifecycleIT {

    @Autowired
    lateinit var endpoint: ExecutionEndPoint

    @Test
    fun testBaseline() {
        endpoint.executeBaseline(GlueLineDTO.create("WCC", "sale of fruit"))
        endpoint.executeBaseline(GlueLineDTO.create("WCC", "sale of widgets"))
    }

    @Test
    fun testAssume() {
        assertThat(endpoint.executeAssumption(GlueLineDTO.create("WCC", "sale of fruit has risen by 70 per cent"))).isTrue()
        assertThat(endpoint.executeAssumption(GlueLineDTO.create("WCC", "sale of fruit has risen by 50 per cent"))).isFalse()

        assertThat(endpoint.executeAssumption(GlueLineDTO.create("WCC", "sale of widgets has risen by 21 per cent"))).isTrue()
        assertThat(endpoint.executeAssumption(GlueLineDTO.create("WCC", "sale of widgets has risen by 19 per cent"))).isFalse()
    }

    @Test
    fun testDuration() {
        assertThat(endpoint.getDuration(GlueLineDTO.create("WCC", "3 weeks"))).isEqualTo(21 * 86400 * 1000)
        assertThat(endpoint.getDuration(GlueLineDTO.create("WCC", "next Easter"))).isEqualTo(10 * 1000)
    }

    @Test
    fun testSuccess() {
        assertThat(endpoint.executeSuccess(GlueLineDTO.create("WCC", "send email to john@wcc.nl")))
        assertThat(endpoint.executeSuccess(GlueLineDTO.create("WCC", "send text to 06-12341234")))
    }

    @Test
    fun testFailure() {
        assertThat(endpoint.executeFailure(GlueLineDTO.create("WCC", "send email to john@wcc.nl")))
        assertThat(endpoint.executeFailure(GlueLineDTO.create("WCC", "send text to 06-12341234")))
    }

}

