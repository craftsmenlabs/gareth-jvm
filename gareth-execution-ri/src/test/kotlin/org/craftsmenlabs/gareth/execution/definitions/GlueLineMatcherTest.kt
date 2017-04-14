package org.craftsmenlabs.gareth.execution.definitions

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.services.DefinitionRegistry
import org.craftsmenlabs.gareth.execution.services.GlueLineMatcherService
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JMockit::class)
class GlueLineMatcherTest {

    @Injectable
    lateinit var definitionRegistry: DefinitionRegistry
    @Tested
    lateinit var matcher: GlueLineMatcherService

    @Before
    fun setup() {
        object : Expectations() {
            init {
                definitionRegistry.getGluelinesPerCategory(GlueLineType.ASSUME)
                result = setOf(Pair("has risen by (\\d{2}) per cent", "has risen by some percentage"),
                        Pair("has increased", "is toegenomen"))
            }
        }
    }

    @Test
    fun getExactMatch() {
        val matches = matcher.getMatches(GlueLineType.ASSUME, "has increased")
        assertThat(matches.suggestions).containsExactly("is toegenomen")
        assertThat(matches.exact).isEqualTo("is toegenomen")
    }

    @Test
    fun getPartialMatch() {
        val matches = matcher.getMatches(GlueLineType.ASSUME, "has")
        assertThat(matches.suggestions).containsExactly("has risen by some percentage", "is toegenomen")
        assertThat(matches.exact).isNull()
    }

    @Test
    fun getExactRegexMatch() {
        val matches = matcher.getMatches(GlueLineType.ASSUME, "has risen by 20 per cent")
        assertThat(matches.suggestions).containsExactly("has risen by some percentage")
        assertThat(matches.exact).isEqualTo("has risen by some percentage")
    }

}