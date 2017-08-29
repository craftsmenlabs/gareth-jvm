package org.craftsmenlabs.gareth.validator.model

import java.time.Duration

data class GlueLineSearchResultDTO(val suggestions: List<String>, val exact: String?)

data class DefinitionRegistryDTO(val timeDefinitions: Map<String, Pair<String, Duration>> = hashMapOf(),
                                 val glueLinesPerCategory: Map<GlueLineType, Set<Glueline>> = hashMapOf())