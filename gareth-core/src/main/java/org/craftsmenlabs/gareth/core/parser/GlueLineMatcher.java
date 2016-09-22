package org.craftsmenlabs.gareth.core.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.craftsmenlabs.gareth.api.model.GlueLineType;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GlueLineMatcher {

    private Map<GlueLineType, Set<String>> glueLinesPerCategory;

    public void init(final Map<GlueLineType, Set<String>> model) {
        this.glueLinesPerCategory = Maps.newHashMap(model);

        Set<String> expandedTimeGlueLines = new HashSet<>(model.get(GlueLineType.TIME));
        expandedTimeGlueLines.addAll(timeGlueLines());
        this.glueLinesPerCategory.put(GlueLineType.TIME, expandedTimeGlueLines);
    }

    public Optional<GlueLineType> getGlueLineType(final String key) {
        return GlueLineType.safeValueOf(key == null ? null : key.trim().toUpperCase());
    }


    public Map<String, List<String>> getMatches(final String glueLineType, final String line) {
        if (line == null || line.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<String> patternsPerGlueLineType = glueLinesPerCategory.get(getGlueLineType(glueLineType).get());
        Map<String, List<String>> output = new HashMap<>();
        output.put("suggestions", getMatchingGlueLines(patternsPerGlueLineType, p -> p
                .contains(line) || isPartialMatch(p, line)));
        output.put("matches", getMatchingGlueLines(patternsPerGlueLineType, p -> isFullMatch(p, line)));
        return output;
    }

    private List<String> getMatchingGlueLines(final Set<String> patterns, Predicate<String> filter) {
        return patterns.stream().filter(filter).map(p -> convertRegexToHumanReadable(p)).collect(Collectors.toList());
    }

    private String convertRegexToHumanReadable(final String pattern) {
        return pattern.replaceAll("^\\^", "").replaceAll("\\$$", "").replaceAll("\\(.+?\\)", "*");
    }

    private boolean isFullMatch(final String pattern, final String test) {
        return matchType(pattern, test) == 2;
    }

    private boolean isPartialMatch(final String pattern, final String test) {
        return matchType(pattern, test) >= 1;
    }

    private int matchType(final String pattern, final String test) {
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(test);
        if (matcher.matches())
            return 2;
        else if (matcher.hitEnd())
            return 1;
        else return 0;
    }

    private List<String> timeGlueLines() {
        return Lists
                .newArrayList("^(\\d+) seconds$", "^(\\d+) minutes$", "^(\\d+) hours$", "^(\\d+) days$", "^(\\d+) weeks$", "^(\\d+) months$", "^(\\d+) years$");
    }
}
