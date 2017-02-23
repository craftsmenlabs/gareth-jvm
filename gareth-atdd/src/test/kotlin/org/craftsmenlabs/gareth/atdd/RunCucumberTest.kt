package org.craftsmenlabs.gareth.atdd

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(features = arrayOf("classpath:features"), format = arrayOf("pretty", "json:target/cucumber-html-reports/testresults.json"))
class RunCucumberTest {

}