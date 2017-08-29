package org.craftsmenlabs.gareth.execution.definitions

import java.util.*


class ParsedDefinition {

    val baselineDefinitions = HashMap<String, InvokableMethod>()
    val assumeDefinitions = HashMap<String, InvokableMethod>()
    val successDefinitions = HashMap<String, InvokableMethod>()
    val failureDefinitions = HashMap<String, InvokableMethod>()
    val timeDefinitions = HashMap<String, InvokableMethod>()
}