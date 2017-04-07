package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.execution.RunContext
import org.junit.Test

class InvokableMethodTest {

    val assume = SaleOfFruit()
    val runContext = RunContext(mutableMapOf())
    val assumeMethod = SaleOfFruit::class.java.getDeclaredMethod("hasRisenByPercent", Integer.TYPE)

    @Test
    fun testInvocation() {
        val invokableMethod = InvokableMethod(glueLine = "^sale of fruit has risen by (\\d+?) per cent$",
                method = assumeMethod, runcontextParameter = false, description = "")
        invokableMethod.invokeWith("sale of fruit has risen by 60 per cent", assume, runContext)

    }

}