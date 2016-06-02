package org.craftsmenlabs.gareth.rest.resource;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.model.GlueLineType;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.core.parser.GlueLineMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionsResourceTest {

    @Mock
    private ExperimentEngine engine;
    @Mock
    GlueLineMatcher glueLineMatcher;
    @Mock
    private DefinitionRegistry registry;
    @InjectMocks
    DefinitionsResource resource;
    private Map<GlueLineType, Set<String>> model = new HashMap<>();

    @Before
    public void setup() {
        when(registry.getGlueLinesPerCategory()).thenReturn(model);
        when(engine.getDefinitionRegistry()).thenReturn(registry);
        resource.init();
    }

    @Test
    public void testInitialization() {
        verify(glueLineMatcher).init(model);
    }

    @Test
    public void testForIllegalGlueLineTypeReturns400() {
        when(glueLineMatcher.getGlueLineType("!@#")).thenReturn(Optional.empty());
        assertThat(resource.getMatches("!@#", null).getStatus()).isEqualTo(400);
    }

    @Test
    public void testForValidGlueLineType() {
        when(glueLineMatcher.getGlueLineType(eq("assume"))).thenReturn(Optional.of(GlueLineType.BASELINE));
        Map matches = mock(Map.class);
        when(glueLineMatcher.getMatches(eq("assume"), eq("sale has increased"))).thenReturn(matches);
        assertThat(resource.getMatches("assume", "sale has increased").getEntity()).isSameAs(matches);
    }

}