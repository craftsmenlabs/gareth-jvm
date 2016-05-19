package org.craftsmenlabs.gareth.rest.resource;

import jersey.repackaged.com.google.common.collect.Sets;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionsResourceTest {

    @Mock
    private ExperimentEngine engine;

    @Mock
    private DefinitionRegistry registry;
    @InjectMocks
    DefinitionsResource resource;
    private Map<String, Set<String>> model = new HashMap<>();
    String regexWithNumber = "^sale of carrots is (\\d+)$";
    String regexWithTwoStrings = "^sale of (.*?) is (.*?)$";

    @Before
    public void setup() {
        when(registry.getGlueLinesPerCategory()).thenReturn(model);
        model.put("baseline", Sets.newHashSet(regexWithNumber, regexWithTwoStrings));
        model.put("time", Sets.newHashSet("^next Easter$"));
        when(engine.getDefinitionRegistry()).thenReturn(registry);
        resource.init();
    }

    @Test
    public void testForIllegalPart() {
        assertThat(resource.getMatches("!@#", null).getStatus()).isEqualTo(400);
    }

    @Test
    public void testForNullStringReturnEmptyList() {
        assertThat(get("baseline", null)).isEmpty();
        assertThat(get("baseline", "")).isEmpty();
    }

    @Test
    public void testForStringOfThreeReturnsListOfTwo() {
        assertThat(get("baseline", "sal").get("suggestions"))
                .containsExactlyInAnyOrder("sale of * is *", "sale of carrots is *");
        assertThat(get("baseline", "sal").get("matches"))
                .isEmpty();
    }

    @Test
    public void testForPatternMatchReturnListOfTwo() {
        assertThat(get("baseline", "sale of carrots is 500").get("suggestions"))
                .containsExactlyInAnyOrder("sale of * is *", "sale of carrots is *");
        assertThat(get("baseline", "sale of carrots is 500").get("matches"))
                .containsExactlyInAnyOrder("sale of * is *", "sale of carrots is *");
    }

    @Test
    public void testForPatternMatchReturnListOfOne() {
        assertThat(get("baseline", "sale of cucumbers is great").get("suggestions"))
                .containsExactlyInAnyOrder("sale of * is *");
        assertThat(get("baseline", "sale of cucumbers is great").get("matches"))
                .containsExactlyInAnyOrder("sale of * is *");
    }

    @Test
    public void testForTimePatternNextEasterReturnsOne() {
        assertThat(get("time", "next Easter").get("suggestions"))
                .containsExactlyInAnyOrder("next Easter");
    }

    @Test
    public void testForValidTimeStrings() {
        assertThat(get("time", "12 days").get("suggestions"))
                .containsExactlyInAnyOrder("* days?");
    }

    Map<String, List<String>> get(String key, String value) {
        Response matches = resource.getMatches(key, value);
        return (Map<String, List<String>>) matches.getEntity();
    }
}