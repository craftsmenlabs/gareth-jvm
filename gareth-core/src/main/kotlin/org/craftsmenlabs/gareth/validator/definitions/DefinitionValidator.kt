package org.craftsmenlabs.gareth.validator.definitions

import org.craftsmenlabs.gareth.validator.beans.DurationExpressionParser
import org.craftsmenlabs.gareth.validator.model.GlueLineSearchResultDTO
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Glueline
import org.craftsmenlabs.gareth.validator.model.Gluelines
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class DefinitionValidator(@Autowired private val durationExpressionParser: DurationExpressionParser,
                          private val definitionRegistryService: DefinitionRegistryService) {
    fun gluelineIsValid(projectId: String, type: GlueLineType, glueline: String?): Boolean {
        if (glueline == null || glueline.isBlank()) {
            //only success and failure are allowed to be blank
            return type == GlueLineType.SUCCESS || type == GlueLineType.FAILURE
        }
        if (type == GlueLineType.TIME)
            return isStandardTimeFormat(glueline) || isValidTimeGlueLine(projectId, glueline)
        else return isValidGlueLine(projectId, type, glueline)
    }

    fun validateGluelines(projectId: String, gluelines: Gluelines): Boolean {
        val lines = mapOf<GlueLineType, String?>(
                Pair(GlueLineType.ASSUME, gluelines.assume),
                Pair(GlueLineType.BASELINE, gluelines.baseline),
                Pair(GlueLineType.TIME, gluelines.time),
                Pair(GlueLineType.FAILURE, gluelines.failure),
                Pair(GlueLineType.SUCCESS, gluelines.success))
        return lines.all { gluelineIsValid(projectId, it.key, it.value) }
    }

    fun lookupGlueline(projectId: String, glueLineType: GlueLineType, line: String): GlueLineSearchResultDTO {
        if (line == null || line.isEmpty()) {
            return GlueLineSearchResultDTO(listOf<String>(), null)
        }
        if (glueLineType == GlueLineType.TIME && isStandardTimeFormat(line)) {
            return GlueLineSearchResultDTO(exact = line, suggestions = listOf(line))
        }
        val registry = definitionRegistryService.getRegistryForClient(projectId)
        val patternsPerGlueLineType: Set<Glueline> = registry.getGluelinesPerCategory(glueLineType)
        val suggestions = getMatchingGlueLines(patternsPerGlueLineType, { it.pattern.contains(line) || isPartialMatch(it.pattern, line) })
        val exact = getMatchingGlueLines(patternsPerGlueLineType, { isFullMatch(it.pattern, line) }).firstOrNull()
        return GlueLineSearchResultDTO(suggestions, exact)
    }

    private fun isStandardTimeFormat(format: String) = durationExpressionParser.parse(format) != null

    /**
     * Connects to the the DefinitionsEndPoint REST controller in execution project
     */
    private fun isValidGlueLine(projectId: String, type: GlueLineType, content: String) = lookupGlueline(projectId, type, content).exact != null

    private fun isValidTimeGlueLine(projectId: String, content: String): Boolean = isValidGlueLine(projectId, GlueLineType.TIME, content)

    private fun getMatchingGlueLines(patterns: Set<Glueline>?, filter: (Glueline) -> Boolean): List<String> {
        return if (patterns == null) listOf() else patterns.filter(filter).map({ it.readable ?: it.pattern })
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