package org.craftsmenlabs.gareth.validator.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.craftsmenlabs.gareth.validator.client.GlueLineExecutor
import org.craftsmenlabs.gareth.validator.model.ExecutionResult
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.model.ExperimentRunEnvironment
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.junit.Test
import java.time.LocalDateTime

class ExperimentExecutorTest {

    @Tested
    private lateinit var executor: ExperimentExecutor
    @Injectable
    private lateinit var glueLineExecutor: GlueLineExecutor
    @Injectable
    private lateinit var dateTimeService: TimeService

    @Injectable
    private lateinit var executionResult: ExecutionResult
    @Injectable
    private lateinit var runEnvironment: ExperimentRunEnvironment

    val now = LocalDateTime.now()
    val dto = ExperimentDTO.createDefault(now)

    @Test
    fun testAssumeWithError() {
        setupAssume()
        object : Expectations() {
            init {
                executionResult.status
                result = ExecutionStatus.ERROR
            }
        }
        val result = executor.executeAssume(dto.copy(status = ExecutionStatus.RUNNING))
        assertThat(result.completed).isEqualTo(now)
        assertThat(result.status).isEqualTo(ExecutionStatus.ERROR)
        assertThat(result.environment).isSameAs(runEnvironment)
    }

    @Test
    fun testSuccessfulAssume() {
        setupAssume()
        object : Expectations() {
            init {
                executionResult.status
                result = ExecutionStatus.SUCCESS
            }
        }
        val result = executor.executeAssume(dto.copy(status = ExecutionStatus.RUNNING))
        assertThat(result.completed).isEqualTo(now)
        assertThat(result.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(result.environment).isSameAs(runEnvironment)
    }

    @Test
    fun testSuccessfulBaseline() {
        setupBaseline()
        object : Expectations() {
            init {
                executionResult.status
                result = ExecutionStatus.RUNNING
            }
        }
        val result = executor.executeBaseline(dto.copy(status = ExecutionStatus.RUNNING))
        assertThat(result.completed).isNull()
        assertThat(result.status).isEqualTo(ExecutionStatus.RUNNING)
        assertThat(result.baselineExecuted).isEqualTo(now)
        assertThat(result.environment).isSameAs(runEnvironment)
    }

    @Test
    fun testBaselineWithError() {
        setupBaseline()
        object : Expectations() {
            init {
                executionResult.status
                result = ExecutionStatus.ERROR
            }
        }
        val result = executor.executeBaseline(dto.copy(status = ExecutionStatus.RUNNING))
        assertThat(result.completed).isEqualTo(now)
        assertThat(result.status).isEqualTo(ExecutionStatus.ERROR)
        assertThat(result.baselineExecuted).isNull()
        assertThat(result.environment).isSameAs(runEnvironment)
    }


    @Test
    fun testRejectedAssume() {
        assertThatThrownBy { executor.executeAssume(dto) }.hasMessage("Can only execute assume when experiment is in state RUNNING, but state is PENDING")
    }


    private fun setupAssume() {
        object : Expectations() {
            init {
                glueLineExecutor.executeAssume(withAny(dto))
                result = executionResult
                dateTimeService.now()
                result = now
                executionResult.environment
                result = runEnvironment
            }
        }
    }

    private fun setupBaseline() {
        object : Expectations() {
            init {
                glueLineExecutor.executeBaseline(withAny(dto))
                result = executionResult
                dateTimeService.now()
                result = now
                executionResult.environment
                result = runEnvironment
            }
        }
    }
}