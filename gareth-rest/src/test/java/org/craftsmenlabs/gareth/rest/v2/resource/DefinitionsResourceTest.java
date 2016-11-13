package org.craftsmenlabs.gareth.rest.v2.resource;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.craftsmenlabs.gareth.api.model.GlueLineType;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.parser.GlueLineMatcher;
import org.craftsmenlabs.gareth.rest.v2.entity.ExperimentToModelMapper;
import org.craftsmenlabs.gareth.rest.v2.resources.DefinitionsResource;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefinitionsResourceTest {

    @Tested
    DefinitionsResource resource;
    @Injectable
    private ExperimentEngine experimentEngine;
    @Injectable
    private GlueLineMatcher glueLineMatcher;
    @Injectable
    private ExperimentToModelMapper mapper;
    private Map<GlueLineType, Set<String>> model = new HashMap<>();

    @Test
    public void testInitialization(@Injectable Map<GlueLineType, Set<String>> param) {
        new Verifications() {{
            glueLineMatcher.init(withAny(param));
        }};
    }

    @Test
    public void testForIllegalGlueLineTypeReturns400() {
        new Expectations() {{
            glueLineMatcher.getGlueLineType("!@#");
            result = Optional.empty();
        }};

        assertThatThrownBy(() -> resource.getMatches("!@#", null))
                .isInstanceOfAny(IllegalArgumentException.class);
    }

    @Test
    public void testForValidGlueLineType(@Injectable Map map) {
        new Expectations() {{
            glueLineMatcher.getGlueLineType("assume");
            result = Optional.of(GlueLineType.BASELINE);

            glueLineMatcher.getMatches("assume", "sale has increased");
            result = map;
        }};
        assertThat(resource.getMatches("assume", "sale has increased")).isSameAs(map);
    }

}