package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.validator.ExperimentDefinition
import org.springframework.stereotype.Service

/**
 * Spring needs at least one implementation of ExperimentDefinition in order to inject a list of them
 */
@Service
class EmptyDefinition : ExperimentDefinition {
}