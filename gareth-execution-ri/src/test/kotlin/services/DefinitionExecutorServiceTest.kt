package services

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.craftsmenlabs.gareth.execution.dto.ExecutionRequestDTO
import org.craftsmenlabs.gareth.execution.services.DefinitionExecutorService
import org.craftsmenlabs.gareth.execution.services.DefinitionService
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JMockit::class)
class DefinitionExecutorServiceTest {

    @Tested
    lateinit var _service: DefinitionExecutorService
    @Injectable
    lateinit var _definitionService: DefinitionService


    @Test
    fun testExecuteBaseline() {

        object : Expectations() {
            init {
                //     gluelineCodeRegistry.executeByType(withEqual("sale of bananas"))
            }
        }
        _service.executeBaseline(ExecutionRequestDTO.create("sale of bananas"))
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
        _service.executeSuccess(ExecutionRequestDTO.create("send sweets"))
    }

    @Test
    fun testExecuteFailureGlueline() {

        object : Expectations() {
            init {
                //  gluelineCodeRegistry.executeFailure(withEqual("Fire the suits"), withInstanceOf(ExecutionRequestDTO::class.java))
            }
        }
        _service.executeFailure(ExecutionRequestDTO.create("Fire the suits"))
    }

    @Test
    fun testGetGlueline() {

        object : Expectations() {
            init {
                _definitionService.getTime(withEqual("2 weeks"))
            }
        }
        _service.getTime(ExecutionRequestDTO.create("2 weeks"))
    }

}