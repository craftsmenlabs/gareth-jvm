package org.craftsmenlabs.gareth.validator.model

data class GlueLineSearchResultDTO(val suggestions: List<String>, val exact: String?)

data class DefinitionRegistryDTO(val glueLinesPerCategory: Map<GlueLineType, Set<Glueline>> = hashMapOf()) {
    fun data() = glueLinesPerCategory
}