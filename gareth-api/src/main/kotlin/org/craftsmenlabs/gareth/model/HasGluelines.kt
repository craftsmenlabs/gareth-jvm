package org.craftsmenlabs.gareth.model

interface HasGlueLines {
    val glueLines: Gluelines
}

data class Gluelines(val baseline: String,
                     val assume: String,
                     val success: String,
                     val failure: String,
                     val time: String) {
    fun getGlueLine(type: GlueLineType): String {
        return when (type) {
            GlueLineType.ASSUME -> assume
            GlueLineType.BASELINE -> baseline
            GlueLineType.SUCCESS -> success
            GlueLineType.FAILURE -> failure
            GlueLineType.TIME -> time
        }
    }
}