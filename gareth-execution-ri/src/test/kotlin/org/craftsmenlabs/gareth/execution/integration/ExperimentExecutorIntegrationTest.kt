package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.GarethExecutionApplication
import org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit
import org.craftsmenlabs.gareth.execution.services.ExecutionService
import org.craftsmenlabs.gareth.validator.model.ExecutionRequest
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus
import org.craftsmenlabs.gareth.validator.model.RunContext
import org.craftsmenlabs.gareth.validator.model.ValidatedGluelines
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(GarethExecutionApplication::class, SaleOfFruit::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
class ExperimentExecutorIntegrationTest {

    @Autowired
    lateinit var executionService: ExecutionService

    @Autowired
    lateinit var timeService: TimeService

    private val fullGluelineSet = ValidatedGluelines(
            baseline = "sale of fruit",
            assume = "sale of fruit has risen by 81 per cent",
            time = "next Easter",
            success = "send email to John",
            failure = "send email to Bob")

    @Test
    fun executeBaselineWithCustomTime() {
        val request = ExecutionRequest("42", RunContext(), fullGluelineSet)
        val result = executionService.executeBaseline(request)
        val inTenDays = timeService.now().plusDays(10)
        assertThat(result.assumptionDue).isEqualToIgnoringSeconds(inTenDays)
        assertThat(result.runContext.getLong("fruit")).isEqualTo(42)

    }

    @Test
    fun executeBaselineWithInvalidItemTypeWillAbort() {
        val request = ExecutionRequest("42", RunContext(), fullGluelineSet.copy(baseline = "sale of iPhones"))
        val result = executionService.executeBaseline(request)
        assertThat(result.assumptionDue).isNull()
        assertThat(result.success).isFalse()
        assertThat(result.runContext.getString("ERROR_DURING_BASELINE")).isEqualTo("Not a valid sale iPhones")
    }

    @Test
    fun executeBaselineWithOneWeek() {
        val request = ExecutionRequest("42", RunContext(), fullGluelineSet.copy(time="1 weeks"))
        val result = executionService.executeBaseline(request)
        val inOneWeek = timeService.now().plusDays(7)
        assertThat(result.assumptionDue).isEqualToIgnoringSeconds(inOneWeek)
    }

    @Test
    fun executeAssumptionWithSuccess() {
        val request = ExecutionRequest("42", RunContext(), fullGluelineSet)
        val result = executionService.executeAssumption(request)
        assertThat(result.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(result.runContext.getString("result")).isEqualToIgnoringCase("sending success mail to John")
    }

    @Test
    fun executeAssumptionWithFailure() {
        val request = ExecutionRequest("42", RunContext(), fullGluelineSet.copy(assume = "sale of fruit has risen by 79 per cent"))
        val result = executionService.executeAssumption(request)
        assertThat(result.status).isEqualTo(ExecutionStatus.FAILURE)
        assertThat(result.runContext.getString("result")).isEqualToIgnoringCase("sending failure mail to Bob")
    }

}