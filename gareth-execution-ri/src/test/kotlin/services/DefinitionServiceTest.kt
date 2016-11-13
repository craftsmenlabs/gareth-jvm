package services

import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.definitions.DefinitionFactory
import org.craftsmenlabs.gareth.execution.services.DefinitionService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JMockit::class)
class DefinitionServiceTest {

    @Tested
    lateinit var _registry: DefinitionService
    @Injectable
    lateinit var definitionFactory: DefinitionFactory

    @Before
    fun setup() {

    }

    @Test
    fun testCorrectDuration() {
        _registry.init()
        assertThat(_registry.getDuration("1 hour")).isEqualTo(3600000)
        assertThat(_registry.getDuration("next Easter")).isEqualTo(10000)

    }
}