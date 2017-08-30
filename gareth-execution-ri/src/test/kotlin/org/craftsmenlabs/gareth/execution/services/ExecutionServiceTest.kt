package org.craftsmenlabs.gareth.execution.services

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import mockit.Verifications
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.validator.beans.DurationExpressionParser
import org.craftsmenlabs.gareth.validator.model.*
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class ExecutionServiceTest {

    @Injectable
    lateinit var definitionFactory: DefinitionFactory
    @Injectable
    lateinit var durationParser: DurationExpressionParser
    var context = RunContext()

    @Tested
    lateinit var service: ExecutionService

    val now = LocalDateTime.now()
    val later = now.plusMonths(1)
    val inTenDays = Duration.ofDays(10)

    @Before
    fun setup() {
        context.storeLong("AMOUNT", 5)

        object : Expectations() {
            init {
                durationParser.calculateTimeDifferenceFromNow(inTenDays)
                result = later
                minTimes = 0
                definitionFactory.getTimeToExecuteAssumption("1 month")
                result = Pair("1 month", inTenDays)
                minTimes=0
            }
        }

    }

    @Test
    fun testExecuteBaseline() {
        val request = ExecutionRequest(experimentId = "42", glueLines = createGluelines(), runContext = context)
        expectInvocation("sale of fruit", GlueLineType.BASELINE)
        val result = service.executeBaseline(request)
        assertThat(result.assumptionDue).isEqualToIgnoringSeconds(later)
        assertThat(result.success).isTrue()
        assertThat(result.runContext.getLong("AMOUNT")).isEqualTo(5)
    }

    @Test
    fun testExecuteBaselineWithException() {
        val request = ExecutionRequest(experimentId = "42", glueLines = createGluelines(), runContext = context)
        expectInvocationWithError("sale of fruit", GlueLineType.BASELINE, IllegalStateException("Oops"))
        val result = service.executeBaseline(request)
        assertThat(result.assumptionDue).isNull()
        assertThat(result.success).isFalse()
        assertThat(result.runContext.getString("ERROR_DURING_BASELINE")).isEqualTo("Oops")
    }

    @Test
    fun testExecuteAssumptionWithSuccess() {
        val request = ExecutionRequest(experimentId = "42", glueLines = createGluelines(), runContext = context)
        expectInvocation("has risen", GlueLineType.ASSUME, true)
        val result = service.executeAssumption(request)
        assertThat(result.runContext.getLong("AMOUNT")).isEqualTo(5)
        assertThat(result.status).isEqualTo(ExecutionStatus.SUCCESS)
        verifyInvocation("celebrate", GlueLineType.SUCCESS)
    }

    @Test
    fun testExecuteAssumeWithException() {
        val request = ExecutionRequest(experimentId = "42", glueLines = createGluelines(), runContext = context)
        expectInvocationWithError("has risen", GlueLineType.ASSUME, IllegalStateException("Oops"))
        val result = service.executeAssumption(request)
        assertThat(result.status).isEqualTo(ExecutionStatus.ERROR)
        assertThat(result.runContext.getString("ERROR_DURING_ASSUME")).isEqualTo("Oops")
    }

    @Test
    fun testExecuteAssumptionWithFailure() {
        val request = ExecutionRequest(experimentId = "42", glueLines = createGluelines(), runContext = context)
        expectInvocation("has risen", GlueLineType.ASSUME, false)
        val result = service.executeAssumption(request)
        assertThat(result.status).isEqualTo(ExecutionStatus.FAILURE)
        verifyInvocation("mourn", GlueLineType.FAILURE)
    }

    @Test
    fun testExecuteAssumptionWithNullResultIsFailure() {
        val request = ExecutionRequest(experimentId = "42", glueLines = createGluelines(), runContext = context)
        expectInvocation("has risen", GlueLineType.ASSUME, null)
        val result = service.executeAssumption(request)
        assertThat(result.status).isEqualTo(ExecutionStatus.FAILURE)
        verifyInvocation("mourn", GlueLineType.FAILURE)
    }


    private fun expectInvocation(glueline: String, type: GlueLineType, returnValue: Boolean? = null) {
        object : Expectations() {
            init {
                definitionFactory.invokeGlueline(glueline, type, context)
                result = returnValue
            }
        }
    }

    private fun expectInvocationWithError(glueline: String, type: GlueLineType, exception: Exception) {
        object : Expectations() {
            init {
                definitionFactory.invokeGlueline(glueline, type, context)
                result = exception
            }
        }
    }

    private fun verifyInvocation(glueline: String, type: GlueLineType) {
        object : Verifications() {
            init {
                definitionFactory.invokeGlueline(glueline, type, context)
            }
        }
    }

    private fun createGluelines() = ValidatedGluelines("sale of fruit", assume = "has risen", time = "1 month", success = "celebrate", failure = "mourn")


}