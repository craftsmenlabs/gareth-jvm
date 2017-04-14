package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.validator.model.GlueLineSearchResultDTO
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
open class GlueLineMatcherService @Autowired constructor(val definitionRegistry: DefinitionRegistry) {

    fun getMatches(glueLineType: GlueLineType, line: String?): GlueLineSearchResultDTO {
        if (line == null || line.isEmpty()) {
            return GlueLineSearchResultDTO(listOf<String>(), null)
        }
        val patternsPerGlueLineType = if (glueLineType == GlueLineType.TIME) definitionRegistry.timeGlueLines else definitionRegistry.getGluelinesPerCategory(glueLineType)
        val suggestions = getMatchingGlueLines(patternsPerGlueLineType, { it.first.contains(line) || isPartialMatch(it.first, line) })
        val exact = getMatchingGlueLines(patternsPerGlueLineType, { isFullMatch(it.first, line) }).firstOrNull()
        return GlueLineSearchResultDTO(suggestions, exact)
    }

    private fun getMatchingGlueLines(patterns: Set<Pair<String, String?>>?, filter: (Pair<String, String?>) -> Boolean): List<String> {
        return if (patterns == null) listOf() else patterns.filter(filter).map({ it.second ?: it.first })
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

}


