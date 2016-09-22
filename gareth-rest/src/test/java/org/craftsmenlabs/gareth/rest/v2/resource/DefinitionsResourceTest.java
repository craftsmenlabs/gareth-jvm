package org.craftsmenlabs.gareth.rest.v2.resource;

import mockit.*;
import org.craftsmenlabs.gareth.api.model.GlueLineType;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.parser.GlueLineMatcher;
import org.craftsmenlabs.gareth.rest.v2.entity.ExperimentToModelMapper2;
import org.craftsmenlabs.gareth.rest.v2.resources.DefinitionsResource;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DefinitionsResourceTest {

    @Injectable
    private ExperimentEngineImpl experimentEngine;

    @Mocked
    private GlueLineMatcher glueLineMatcher;

    @Injectable
    private ExperimentToModelMapper2 mapper;

    @Tested
    DefinitionsResource resource;

    private Map<GlueLineType, Set<String>> model = new HashMap<>();

//    @Before
//    public void setup() {
//        new Expectations(){{
//
//        }};
//        when(registry.getGlueLinesPerCategory()).thenReturn(model);
//        when(engine.getDefinitionRegistry()).thenReturn(registry);
//        resource.init();
//    }

    @Test
    public void testInitialization(@Injectable Map<GlueLineType, Set<String>> param) {
        new Verifications(){{
            glueLineMatcher.init(withAny(param));
        }};
    }

    @Test
    public void testForIllegalGlueLineTypeReturns400() {
        new Expectations() {{
            glueLineMatcher.getGlueLineType("!@#");
            result = Optional.empty();
        }};

        assertThat(resource.getMatches("!@#", null).getStatus()).isEqualTo(400);
    }

    @Test
    public void testForValidGlueLineType(@Injectable Map map) {
        new Expectations() {{
            glueLineMatcher.getGlueLineType("assume");
            result = Optional.of(GlueLineType.BASELINE);

            glueLineMatcher.getMatches("assume", "sale has increased");
            result = map;
        }};
        assertThat(resource.getMatches("assume", "sale has increased").getEntity()).isSameAs(map);
    }

}