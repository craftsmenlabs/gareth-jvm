package org.craftsmenlabs.gareth.execution.integration

import com.jayway.awaitility.Awaitility
import org.craftsmenlabs.gareth.execution.GarethExecutionApplication
import org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus
import org.craftsmenlabs.gareth.validator.model.ValidatedGluelines
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(GarethExecutionApplication::class, SaleOfFruit::class, MockGarethHub::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
class ExperimentLifecycleIntegrationTest {

    @Autowired
    lateinit var mockExecutionClient: MockGarethHub

    private val fullGluelineSet = ValidatedGluelines(
            baseline = "sale of fruit",
            assume = "sale of fruit has risen by 81 per cent",
            time = "next Easter",
            success = "send email to John",
            failure = "send email to Bob")

    private val noFinalizationGluelineSet = ValidatedGluelines(
            baseline = "sale of fruit",
            assume = "sale of fruit has risen by 81 per cent",
            time = "next Easter")

    private val failingBaselineGluelineSet = ValidatedGluelines(
            baseline = "get snake oil",
            assume = "sale of fruit has risen by 81 per cent",
            time = "2 days")

    @Test
    fun runSuccessful() {
       runExperimentWithResult(fullGluelineSet, ExecutionStatus.SUCCESS)
    }

    @Test
    fun runFailure() {
        runExperimentWithResult(fullGluelineSet.copy(assume = "sale of fruit has risen by 79 per cent"), ExecutionStatus.FAILURE)
    }

    @Test
    fun runSuccessfulWithoutFinalization() {
        runExperimentWithResult(noFinalizationGluelineSet, ExecutionStatus.SUCCESS)
    }

    @Test
    fun runWithAbortingBaseline() {
        mockExecutionClient.addExperimentToExecute("42", failingBaselineGluelineSet)
        Awaitility.waitAtMost(10, TimeUnit.SECONDS).until<Boolean> {
           mockExecutionClient.baselineExecutionResults["42"]?.success == false
        }
    }

    fun runExperimentWithResult(gluelineSet: ValidatedGluelines, status: ExecutionStatus) {
        mockExecutionClient.addExperimentToExecute("42", gluelineSet)
        Awaitility.waitAtMost(10, TimeUnit.SECONDS).until<Boolean> {
            mockExecutionClient.registry != null &&
                    mockExecutionClient.assumptionExecutionResults["42"]?.status == status
        }
    }

}

