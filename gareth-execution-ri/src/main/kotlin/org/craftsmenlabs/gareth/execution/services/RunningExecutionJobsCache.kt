package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.validator.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Keeps track of all glueline executions currently in process, to avoid executing the same job twice.
 */
@Service
class RunningExecutionJobsCache@Autowired constructor(private val timeService: TimeService) {

    @Value("\${cache.expiry}")
    private var cacheExpirySeconds: Long = 120
    private val cache: MutableMap<String, LocalDateTime> = ConcurrentHashMap()

    fun registerExperiment(id: String) {
        if (isInProgress(id))
            throw IllegalStateException("Experiment $id still in progress")
        cache[id] = timeService.now()
    }

    fun popExperiment(id: String) {
        cache.remove(id)
    }

    fun isInProgress(id: String): Boolean = cache.containsKey(id)

    fun isWithinTimeframe(id: String): Boolean {
        val expiryThreshold = timeService.now().minusSeconds(cacheExpirySeconds)
        return cache[id]!!.isAfter(expiryThreshold)
    }

    fun canSkip(experimentId: String): Boolean = isInProgress(experimentId) && isWithinTimeframe(experimentId)


}