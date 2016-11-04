package services

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.dto.GlueLineDTO
import org.craftsmenlabs.gareth.execution.services.GluelineCodeExecutorService
import org.craftsmenlabs.gareth.execution.services.GluelineCodeRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JMockit::class)
class GluelineCodeExecutorServiceTest {

    @Tested
    lateinit var service: GluelineCodeExecutorService
    @Injectable
    lateinit var gluelineCodeRegistry: GluelineCodeRegistry


    @Test
    fun testExecuteBaseline() {

        object : Expectations() {
            init {
                gluelineCodeRegistry.executeBaseline(withEqual("sale of bananas"))
            }
        }
        service.executeBaseline(GlueLineDTO.create("WCC", "sale of bananas"))
    }

    @Test
    fun testExecuteAssumptionlineWithFailure() {

        object : Expectations() {
            init {
                gluelineCodeRegistry.executeAssumption(withEqual("has risen"))
            }
        }
        assertThat(service.executeAssumption(GlueLineDTO.create("WCC", "has risen"))).isTrue()
    }

    @Test
    fun testExecuteAssumptionlineWithSuccess() {

        object : Expectations() {
            init {
                gluelineCodeRegistry.executeAssumption(withEqual("has risen"))
                result = IllegalStateException("Oops!")
            }
        }
        assertThat(service.executeAssumption(GlueLineDTO.create("WCC", "has risen"))).isFalse()
    }

    @Test
    fun testExecuteSuccessGlueline() {

        object : Expectations() {
            init {
                gluelineCodeRegistry.executeSuccess(withEqual("send sweets"))
            }
        }
        service.executeSuccess(GlueLineDTO.create("WCC", "send sweets"))
    }

    @Test
    fun testExecuteFailureGlueline() {

        object : Expectations() {
            init {
                gluelineCodeRegistry.executeFailure(withEqual("Fire the suits"))
            }
        }
        service.executeFailure(GlueLineDTO.create("WCC", "Fire the suits"))
    }

    @Test
    fun testGetGlueline() {

        object : Expectations() {
            init {
                gluelineCodeRegistry.getDurationInMillis(withEqual("2 weeks"))
            }
        }
        service.getDurationInMillis(GlueLineDTO.create("WCC", "2 weeks"))
    }

}