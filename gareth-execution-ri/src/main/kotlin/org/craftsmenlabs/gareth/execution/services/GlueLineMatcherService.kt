package org.craftsmenlabs.gareth.execution.services

import com.google.common.collect.Lists
import org.craftsmenlabs.gareth.api.model.GlueLineType
import org.craftsmenlabs.gareth.execution.dto.GlueLineSearchResultDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern
import javax.annotation.PostConstruct

@Service
open class GlueLineMatcherService @Autowired constructor(val definitionRegistry: DefinitionRegistry) {

    lateinit var glueLinesPerCategory: Map<GlueLineType, Set<String>>
    var timeGlueLines: MutableSet<String> = mutableSetOf<String>()

    @PostConstruct
    fun init() {
        glueLinesPerCategory = definitionRegistry.getGlueLinesPerCategory()
        timeGlueLines.addAll(createDefaultTimeGlueLinePatterns())
        timeGlueLines.addAll(glueLinesPerCategory[GlueLineType.TIME].orEmpty())
    }

    fun getMatches(glueLineType: GlueLineType, line: String?): GlueLineSearchResultDTO {
        if (line == null || line.isEmpty()) {
            return GlueLineSearchResultDTO(listOf<String>(), null)
        }
        val patternsPerGlueLineType = if (glueLineType == GlueLineType.TIME) timeGlueLines else glueLinesPerCategory[glueLineType]
        val suggestions = getMatchingGlueLines(patternsPerGlueLineType, { it.contains(line) || isPartialMatch(it, line) })
        val exact = getMatchingGlueLines(patternsPerGlueLineType, { isFullMatch(it, line) }).firstOrNull()
        return GlueLineSearchResultDTO(suggestions, exact)
    }

    private fun getMatchingGlueLines(patterns: Set<String>?, filter: (String) -> Boolean): List<String> {
        return if (patterns == null) listOf() else patterns.filter(filter).map({ convertRegexToHumanReadable(it) })
    }

    private fun convertRegexToHumanReadable(pattern: String): String {
        return pattern.replace("^\\^".toRegex(), "").replace("\\$$".toRegex(), "").replace("\\(.+?\\)".toRegex(), "*")
    }

    private fun isFullMatch(pattern: String, test: String): Boolean {
        return matchType(pattern, test) == 2
    }

    private fun isPartialMatch(pattern: String, test: String): Boolean {
        return matchType(pattern, test) >= 1
    }

    private fun matchType(pattern: String, test: String): Int {
        val compile = Pattern.compile(pattern)
        val matcher = compile.matcher(test)
        if (matcher.matches())
            return 2
        else if (matcher.hitEnd())
            return 1
        else
            return 0
    }

    private fun createDefaultTimeGlueLinePatterns(): List<String> {
        return Lists.newArrayList("^(\\d+) seconds?$", "^(\\d+) minutes?$", "^(\\d+) hours?$", "^(\\d+) days?$", "^(\\d+) weeks?$", "^(\\d+) months?$", "^(\\d+) years?$")
    }
}


