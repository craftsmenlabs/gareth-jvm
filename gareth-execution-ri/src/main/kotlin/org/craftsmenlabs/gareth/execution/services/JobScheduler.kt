package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.execution.services.definitions.GarethHubClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
open class JobScheduler @Autowired constructor(private val hubClient: GarethHubClient) {

    val logger: Logger = LoggerFactory.getLogger(JobScheduler::class.java)

    @Scheduled(initialDelay = 200, fixedRate = 5000)
    fun runJobs() {
        logger.info("Running baseline tasks")
        hubClient.runBaselineTasks()
        logger.info("Running assume tasks")
        hubClient.runAssumeTasks()
    }


}