package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.validator.model.DefinitionRegistryDTO
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Glueline
import java.util.*
import java.util.regex.Pattern


data class DefinitionsRegistry(private val data: Map<GlueLineType, Map<String, InvokableMethod>>) {
    private val regexes = HashMap<String, Pattern>()

    fun getMethod(glueLine: String, type: GlueLineType): InvokableMethod? {
        return data[type]!!.values.filter({ md -> matchesPattern(glueLine, md.getRegexPatternForGlueLine()) }).firstOrNull()
    }

    fun toRegistryDTO(): DefinitionRegistryDTO = DefinitionRegistryDTO(data.mapValues { it.value.map { Glueline(it.key, it.value.humanReadable) }.toSet() })

    private fun matchesPattern(experimentLine: String, pattern: String): Boolean {
        if (pattern.isBlank())
            return false
        return getGlueLinePattern(pattern).matcher(experimentLine).matches()
    }

    private fun getGlueLinePattern(pattern: String): Pattern {
        if (regexes.containsKey(pattern)) {
            return regexes[pattern]!!
        }
        val compiled = Pattern.compile(pattern)
        regexes.put(pattern, compiled)
        return compiled
    }
}