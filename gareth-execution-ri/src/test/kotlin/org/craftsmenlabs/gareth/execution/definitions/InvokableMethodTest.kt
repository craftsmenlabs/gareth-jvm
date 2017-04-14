package org.craftsmenlabs.gareth.execution.definitions

import org.assertj.core.api.Assertions
import org.craftsmenlabs.gareth.execution.RunContext
import org.junit.Test

class InvokableMethodTest {

    val assume = SaleOfFruit()
    val runContext = RunContext(mutableMapOf())
    val assumeMethod = SaleOfFruit::class.java.getDeclaredMethod("hasRisenByPercent", Integer.TYPE)
    val failingBaselineMethod = SaleOfFruit::class.java.getDeclaredMethod("getSaleOfSnakeOil")
    @Test
    fun testInvocation() {
        val invokableMethod = InvokableMethod(glueLine = "^sale of fruit has risen by (\\d+?) per cent$",
                method = assumeMethod, runcontextParameter = false, description = "beschrijving", humanReadable = "leesbaar voor mensen")
        invokableMethod.invokeWith("sale of fruit has risen by 60 per cent", assume, runContext)
    }

    @Test
    fun testInvocationOfFailingMethod() {
        val invokableMethod = InvokableMethod(glueLine = "^sale of snake oil$",
                method = failingBaselineMethod, runcontextParameter = false, description = "beschrijving", humanReadable = "leesbaar voor mensen")
        Assertions.assertThatThrownBy { invokableMethod.invokeWith("sale of snake oil", assume, runContext) }.hasMessage("There's no such thing as snake oil")
    }
}