package org.craftsmenlabs.gareth2.atdd

import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test

class TestEnvironment {

    @Test
    @Ignore
    fun testSetupGarethEnvironments() {
        val gareth = GarethServerEnvironment.createGarethInstance()
        val execution = GarethServerEnvironment.createExecutionInstance()
        assertThat(gareth.getStatus().isIdle())
        assertThat(execution.getStatus().isIdle())
        gareth.start()
        execution.start()
        assertThat(gareth.getStatus().isStarted())
        assertThat(execution.getStatus().isStarted())
        gareth.shutdown()
        execution.shutdown()
        assertThat(gareth.getStatus().isStopped())
        assertThat(execution.getStatus().isStopped())
    }

}