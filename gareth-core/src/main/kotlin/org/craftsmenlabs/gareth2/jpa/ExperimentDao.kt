package org.craftsmenlabs.gareth2.jpa

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ExperimentDao : CrudRepository<ExperimentEntity, Long> {
    fun findByName(name: String): ExperimentEntity?
}
