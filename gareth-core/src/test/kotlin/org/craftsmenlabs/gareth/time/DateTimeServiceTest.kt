package org.craftsmenlabs.gareth.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

class DateTimeServiceTest {


    val service = DateTimeService()

    @Test
    fun testsecondsInFuture() {
        val inAnHour = LocalDateTime.now().plusHours(1)
        assertThat(service.getSecondsUntil(inAnHour)).isBetween(3599,3601)
    }
}