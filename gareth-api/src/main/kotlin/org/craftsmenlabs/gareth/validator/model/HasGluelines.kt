package org.craftsmenlabs.gareth.validator.model

interface HasGlueLines {
    val glueLines: Gluelines
}

data class Gluelines(val baseline: String?,
                     val assume: String?,
                     val time: String?,
                     val success: String? = null,
                     val failure: String? = null)

data class ValidatedGluelines(val baseline: String,
                              val assume: String,
                              val time: String,
                              val success: String? = null,
                              val failure: String? = null)