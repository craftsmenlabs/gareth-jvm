package org.craftsmenlabs.gareth.atdd

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(features = arrayOf("classpath:features"),
        //tags = arrayOf("@delayed-start"),
        format = arrayOf("pretty", "json:target/cucumber-html-reports/testresults.json"))
class RunCucumberTest {

    companion object {

        val testMode: Boolean = System.getProperty("testMode", "").isNotBlank()
        @BeforeClass @JvmStatic
        fun startup() {
            if (!testMode) {
                GarethServerEnvironment.addInstance(GarethServerEnvironment.createGarethInstance())
                GarethServerEnvironment.addInstance(GarethServerEnvironment.createExecutionInstance())
                GarethServerEnvironment.start()
            }
        }

        @AfterClass @JvmStatic
        fun teardown() {
            if (!testMode) {
                GarethServerEnvironment.shutDown()
            }
        }
    }


}