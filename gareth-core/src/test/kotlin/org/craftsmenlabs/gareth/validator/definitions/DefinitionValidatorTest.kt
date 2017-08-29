package org.craftsmenlabs.gareth.validator.definitions

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.ClientDefinitionsSampleFactory
import org.craftsmenlabs.gareth.validator.beans.DurationExpressionParser
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Gluelines
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.temporal.ChronoUnit


class DefinitionValidatorTest {

    @Injectable
    lateinit var parser: DurationExpressionParser
    @Injectable
    lateinit var registryService: DefinitionRegistryService
    @Tested
    lateinit var validator: DefinitionValidator
    val lines = Gluelines(baseline = "sale of apples",
            assume = "has risen by 20 percent",
            time = "next Easter",
            success = "send cake to developers",
            failure = "fire Bill")
    @Before
    fun setup() {
        val sample = ClientDefinitionsSampleFactory.create()
        object : Expectations() {
            init {
                parser.parse("1 day")
                result = Duration.of(1, ChronoUnit.DAYS)
                minTimes = 0
                registryService.getRegistryForClient("ACME")
                result = sample
                minTimes=0
            }
        }
    }

    @Test
    fun lookupExactBaseline() {
        val dto = validator.lookupGlueline("ACME", GlueLineType.BASELINE, "sale of apples")
        assertThat(dto.exact!!).isEqualTo("sale of <product>")
        assertThat(dto.suggestions[0]).isEqualTo("sale of <product>")
    }

    @Test
    fun lookupFuzzyBaseline() {
        val dto = validator.lookupGlueline("ACME", GlueLineType.BASELINE, "sale")
        assertThat(dto.exact).isNull()
        assertThat(dto.suggestions[0]).isEqualTo("sale of <product>")
    }

    @Test
    fun lookupAssumeWithOneMatch() {
        val dto = validator.lookupGlueline("ACME", GlueLineType.ASSUME, "has risen by 20 percent")
        assertThat(dto.exact).isEqualTo("has risen by <number> percent")
        assertThat(dto.suggestions[0]).isEqualTo("has risen by <number> percent")
    }

    @Test
    fun lookupAssumeWithTwoMatches() {
        val dto = validator.lookupGlueline("ACME", GlueLineType.ASSUME, "has risen by")
        assertThat(dto.exact).isNull()
        assertThat(dto.suggestions).contains("has risen by <number> percent")
        assertThat(dto.suggestions).contains("has risen by <number> items")
    }

    @Test
    fun lookupWithNonMatch() {
        val dto = validator.lookupGlueline("ACME", GlueLineType.ASSUME, "has risen by twenty percent")
        assertThat(dto.exact).isNull()
        assertThat(dto.suggestions).isEmpty()
    }

    @Test
    fun testStandardTime() {
        val dto = validator.lookupGlueline("ACME", GlueLineType.TIME, "1 day")
        assertThat(validator.gluelineIsValid("ACME", GlueLineType.TIME, "1 day")).isTrue()
        assertThat(dto.exact).isEqualTo("1 day")
        assertThat(dto.suggestions[0]).isEqualTo("1 day")
    }

    @Test
    fun testCustomTime() {
        val dto = validator.lookupGlueline("ACME", GlueLineType.TIME, "next Easter")
        assertThat(validator.gluelineIsValid("ACME", GlueLineType.TIME, "next Easter")).isTrue()
        assertThat(dto.exact).isEqualTo("next Easter")
        assertThat(dto.suggestions[0]).isEqualTo("next Easter")
    }


    @Test
    fun testallAreValid() {
        assertThat(validator.validateGluelines("ACME", lines)).isTrue()
    }

    @Test
    fun testOneLineIsInvalid() {
        assertThat(validator.validateGluelines("ACME", lines.copy(failure = "Reward Bill"))).isFalse()
    }

    @Test
    fun testisValid() {
        assertThat(validator.gluelineIsValid("ACME",GlueLineType.BASELINE,lines.baseline)).isTrue()
        assertThat(validator.gluelineIsValid("ACME",GlueLineType.ASSUME,lines.assume)).isTrue()
        assertThat(validator.gluelineIsValid("ACME",GlueLineType.TIME,lines.time)).isTrue()
        assertThat(validator.gluelineIsValid("ACME",GlueLineType.SUCCESS,lines.success)).isTrue()
        assertThat(validator.gluelineIsValid("ACME",GlueLineType.FAILURE,lines.failure)).isTrue()
    }
}