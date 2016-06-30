package org.craftsmenlabs.gareth.core.parser;

import com.google.common.collect.Sets;
import org.craftsmenlabs.gareth.api.model.GlueLineType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GlueLineMatcherTest {
    private final String regexWithNumber = "^sale of carrots has risen by (\\d+?) per cent$";
    private final String regexWithTwoStrings = "^sale of (.*?) is (.*?)$";

    private Map<GlueLineType, Set<String>> model = new HashMap<>();

    @InjectMocks
    private GlueLineMatcher glueLineMatcher;

    @Before
    public void setup() {
        model.put(GlueLineType.BASELINE, Sets.newHashSet(regexWithNumber, regexWithTwoStrings));
        model.put(GlueLineType.TIME, Sets.newHashSet("^next Easter$"));
        glueLineMatcher.init(model);
    }

    @Test
    public void testForNullStringReturnEmptyList() {
        assertThat(glueLineMatcher.getMatches("baseline", null)).isEmpty();
        assertThat(glueLineMatcher.getMatches("baseline", "")).isEmpty();
    }

    @Test
    public void testForStringOfThreeReturnsListOfTwo() {
        assertSuggestionContains("sal", "sale of * is *", "sale of carrots has risen by * per cent");
        assertThat(glueLineMatcher.getMatches("baseline", "sal").get("matches"))
                .isEmpty();
    }

    @Test
    public void testForPatternMatchReturnListOfOne() {
        assertSuggestionContains("sale of cucumbers is great", "sale of * is *");
        assertMatchContains("sale of cucumbers is great", "sale of * is *");
    }

    @Test
    public void testForPartialMatch() {
        assertSuggestionContains("sale of cucumbers", "sale of * is *");
    }

    @Test
    public void testForTimePatternNextEasterReturnsOne() {
        assertThat(glueLineMatcher.getMatches("time", "next Easter").get("suggestions"))
                .containsExactlyInAnyOrder("next Easter");
    }

    @Test
    public void testForValidTimeStrings() {
        assertThat(glueLineMatcher.getMatches("time", "12 days").get("suggestions"))
                .containsExactlyInAnyOrder("* days");
    }

    private void assertSuggestionContains(String line, String... values) {
        assertThat(glueLineMatcher.getMatches("baseline", line).get("suggestions"))
                .containsExactlyInAnyOrder(values);
    }

    private void assertMatchContains(String line, String... values) {
        assertThat(glueLineMatcher.getMatches("baseline", line).get("matches"))
                .containsExactlyInAnyOrder(values);
    }
}