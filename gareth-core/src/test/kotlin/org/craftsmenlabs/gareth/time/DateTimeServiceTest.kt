package org.craftsmenlabs.gareth.time

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.craftsmenlabs.gareth.validator.beans.DateTimeService
import org.craftsmenlabs.gareth.validator.model.DateTimeDTO
import org.craftsmenlabs.gareth.validator.model.ExecutionInterval
import org.junit.Test
import java.time.LocalDateTime

class DateTimeServiceTest {


    val service = DateTimeService()

    @Test
    fun testoneHourInFuture() {
        val inAnHour = LocalDateTime.now().plusHours(1)
        assertThat(service.getSecondsUntil(inAnHour)).isBetween(3599, 3601)
    }

    @Test
    fun test3SecondsInFuture() {
        val threeSeconds = LocalDateTime.now().plusSeconds(3)
        assertThat(service.getSecondsUntil(threeSeconds)).isBetween(2, 4)
    }


    @Test
    fun testDTOConversion() {
        val dto = DateTimeDTO(year = 2015, month = 8, day = 12, hour = 6, minute = 4, seconds = 42)
        val dt = service.toDate(dto)
        assertThat(dt.year).isEqualTo(2015)
        assertThat(dt.month.value).isEqualTo(8)
        assertThat(dt.dayOfMonth).isEqualTo(12)
        assertThat(dt.hour).isEqualTo(6)
        assertThat(dt.minute).isEqualTo(4)
        assertThat(dt.second).isEqualTo(42)

        val roundTrip = DateTimeDTO(dt)
        assertThat(roundTrip).isEqualTo(dto)
    }

    @Test
    fun testExecutionInterval() {
        val now = service.now().withYear(2017).withMonth(1).withDayOfMonth(1)
        assertThatThrownBy { service.getDelay(now, ExecutionInterval.NO_REPEAT) }.hasMessage("Interval cannot be ONCE")
        assertThat(service.getDelay(now, ExecutionInterval.DAILY).dayOfMonth).isEqualTo(2)
        assertThat(service.getDelay(now, ExecutionInterval.WEEKLY).dayOfMonth).isEqualTo(8)
        assertThat(service.getDelay(now, ExecutionInterval.BIWEEKLY).dayOfMonth).isEqualTo(15)
        assertThat(service.getDelay(now, ExecutionInterval.MONTHLY).dayOfMonth).isEqualTo(1)

    }
}