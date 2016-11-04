package services

import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.craftsmenlabs.gareth.execution.invoker.DefinitionFactory
import org.craftsmenlabs.gareth.execution.services.GluelineCodeRegistry
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(JMockit::class)
class GluelineCodeRegistryTest {

    @Tested
    lateinit var registry: GluelineCodeRegistry
    @Injectable
    lateinit var definitionFactory: DefinitionFactory

    @Before
    fun setup() {

    }

/*    @Test
    fun testCorrectBaselines() {
        assertThat(registry.getBaselineMethod("sale of fruit").regexPatternForGlueLine).isEqualTo("^sale of (.*?)$")
        registry.getBaselineMethod("sale of widgets")
    }*/

    /* @Test
     fun testCorrectAssumptions() {
         registry.init()
         registry.getAssumptionMethod("sale of fruit has risen by 5 per cent")
         registry.getAssumptionMethod("sale of widgets has risen by 5 per cent")
     }

     @Test
     fun testCorrectSuccess() {
         registry.init()
         registry.getSuccessMethod("send email to bob")
     }

     @Test
     fun testCorrectFailures() {
         registry.init()
         registry.getFailureMethod("send email to bob")
     }


     @Test
     fun testCorrectDuration() {
         registry.init()
         assertThat(registry.getDurationInMillis("1 hour")).isEqualTo(3600000)
         assertThat(registry.getDurationInMillis("next Easter")).isEqualTo(10000)

     }*/
}