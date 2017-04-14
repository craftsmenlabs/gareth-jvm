package org.craftsmenlabs.gareth.validator.client


interface ExecutionServiceDiscovery {
    fun createUrl(projectId: String, affix: String): String
}