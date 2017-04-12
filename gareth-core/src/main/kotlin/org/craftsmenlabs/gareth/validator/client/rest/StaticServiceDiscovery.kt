package org.craftsmenlabs.gareth.validator.client.rest

import org.craftsmenlabs.gareth.validator.client.ExecutionServiceDiscovery
import org.craftsmenlabs.gareth.validator.mongo.ProjectDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class StaticServiceDiscovery @Autowired constructor(val projectDao: ProjectDao) : ExecutionServiceDiscovery {

    val urlMap = mapOf<String, String>(
            Pair("sale of fruit", "http://localhost:8101"),
            Pair("storynator", "http://localhost:8090"))

    private fun getHostForProject(projectId: String): String {
        val project = projectDao.findOne(projectId) ?: throw IllegalArgumentException("Unknown project $projectId")
        return urlMap[project.name] ?: throw IllegalArgumentException("Project with name ${project.name} is not mapped to a url")
    }

    override fun createUrl(projectId: String, affix: String): String {
        val host = getHostForProject(projectId)
        return "$host/gareth/validator/v1/$affix"
    }
}