package integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.Application
import org.craftsmenlabs.gareth.execution.dto.ExecutionRequestDTO
import org.craftsmenlabs.gareth.execution.dto.ExecutionStatus
import org.craftsmenlabs.gareth.execution.rest.v1.ExecutionEndPoint
import org.craftsmenlabs.gareth.execution.spi.MockDB
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
    @Autowired
    lateinit var mockDB: MockDB

    @Test
    fun testBaseline() {
        assertThat(endpoint.executeBaseline(ExecutionRequestDTO.create("sale of fruit")).status).isEqualTo(ExecutionStatus.RUNNING)
        endpoint.executeBaseline(ExecutionRequestDTO.create("sale of widgets"))
    }

    @Test
    fun testAssume() {
        assertThat(endpoint.executeAssumption(ExecutionRequestDTO.create("sale of fruit has risen by 70 per cent")).status)
                .isEqualTo(ExecutionStatus.RUNNING)
        assertThat(endpoint.executeAssumption(ExecutionRequestDTO.create("sale of fruit has risen by 50 per cent")).status)
                .isEqualTo(ExecutionStatus.RUNNING)

    }

    @Test
    fun testDuration() {
        assertThat(endpoint.getDuration(ExecutionRequestDTO.create("3 weeks"))).isEqualTo(21 * 86400 * 1000)
        assertThat(endpoint.getDuration(ExecutionRequestDTO.create("next Easter"))).isEqualTo(10 * 1000)
    }

    @Test
    fun testSuccess() {
        assertThat(endpoint.executeSuccess(ExecutionRequestDTO.create("send email to john@wcc.nl")).status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(endpoint.executeSuccess(ExecutionRequestDTO.create("send text to 06-12341234")))
    }

    @Test
    fun testFailure() {
        assertThat(endpoint.executeFailure(ExecutionRequestDTO.create("send email to john@wcc.nl")).status).isEqualTo(ExecutionStatus.FAILURE)
        assertThat(endpoint.executeFailure(ExecutionRequestDTO.create("send text to 06-12341234")))
    }

    @Test
    fun testLifeCycle() {
        endpoint.executeBaseline(ExecutionRequestDTO.create("sale of apples"))
        endpoint.executeAssumption(ExecutionRequestDTO.create("sale of fruit has risen by 21 per cent"))
        endpoint.executeSuccess(ExecutionRequestDTO.create("send email to john@wcc.nl"));
        assertThat(mockDB.value).isEqualTo("run10. success email for apples")
    }

}

