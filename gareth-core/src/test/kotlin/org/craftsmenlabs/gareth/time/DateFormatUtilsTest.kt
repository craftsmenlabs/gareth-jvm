package org.craftsmenlabs.gareth.time

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.validator.time.DateFormatUtils
import org.junit.Test

class DateFormatUtilsTest {

    @Test
    fun testParseTime() {
        val time = DateFormatUtils.parseDateTimeString("2017-02-09 11:16:42")
        assertThat(time.dayOfMonth).isEqualTo(9)
        assertThat(time.monthValue).isEqualTo(2)
        assertThat(time.year).isEqualTo(2017)
        assertThat(time.hour).isEqualTo(11)
        assertThat(time.minute).isEqualTo(16)
        assertThat(time.second).isEqualTo(42)
    }

}