package org.craftsmenlabs.gareth.execution.dto

class GlueLineDTO() {
    lateinit var project: String
    lateinit var runId: String
    lateinit var glueline: String


    companion object {
        fun create(project: String, glueline: String): GlueLineDTO {
            val dto = GlueLineDTO()
            dto.project = project
            dto.glueline = glueline
            return dto
        }
    }


}

