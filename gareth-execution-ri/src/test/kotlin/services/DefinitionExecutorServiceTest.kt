package services

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.craftsmenlabs.gareth.execution.services.DefinitionService
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JMockit::class)
class DefinitionExecutorServiceTest {

    @Tested
    @Injectable
    lateinit var _definitionService: DefinitionService


    @Test
    fun testExecuteBaseline() {

        object : Expectations() {
            init {
                //     gluelineCodeRegistry.executeByType(withEqual("sale of bananas"))
            }
        }
    }

    @Test
    fun testExecuteAssumptionlineWithFailure() {

        object : Expectations() {
            init {
                //  gluelineCodeRegistry.executeAssumption(withEqual("has risen"), withInstanceOf(ExecutionRequestDTO::class.java))
            }
        }
        //assertThat(service.executeAssumption(ExecutionRequestDTO.create( "has risen"))).isTrue()
    }

    @Test
    fun testExecuteAssumptionlineWithSuccess() {

        object : Expectations() {
            init {
                //gluelineCodeRegistry.executeAssumption(withEqual("has risen"), withInstanceOf(ExecutionRequestDTO::class.java))
                result = IllegalStateException("Oops!")
            }
        }
        //assertThat(service.executeAssumption(ExecutionRequestDTO.create( "has risen"))).isFalse()
    }

    @Test
    fun testExecuteSuccessGlueline() {

        object : Expectations() {
            init {
                // gluelineCodeRegistry.executeSuccess(withEqual("send sweets"),withInstanceOf(ExecutionRequestDTO::class.java))
            }
        }
    }

    @Test
    fun testExecuteFailureGlueline() {

        object : Expectations() {
            init {
                //  gluelineCodeRegistry.executeFailure(withEqual("Fire the suits"), withInstanceOf(ExecutionRequestDTO::class.java))
            }
        }
    }

    @Test
    fun testGetGlueline() {

        object : Expectations() {
            init {
                _definitionService.getTime(withEqual("2 weeks"))
            }
        }
    }

}