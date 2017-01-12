package org.craftsmenlabs.gareth2.monitors

import mockit.Injectable
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.GlueLineLookup
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.craftsmenlabs.gareth2.util.TestApplication
import org.craftsmenlabs.gareth2.util.WrappedDateTimeService
import org.craftsmenlabs.gareth2.util.WrappedGlueLineExecutor
import org.craftsmenlabs.gareth2.util.WrappedGlueLineLookup
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(TestApplication::class))
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
class MonitorIT {
    // TODO: Hier bende gebleven

    @Injectable lateinit var glueLineExecutor: GlueLineExecutor
    @Injectable lateinit var glueLineLookup: GlueLineLookup
    @Injectable lateinit var dateTimeService: DateTimeService

    @Autowired
    lateinit var monitors: List<BaseMonitor>

    @Autowired
    lateinit var wrappedGlueLineExecutor: WrappedGlueLineExecutor

    @Autowired
    lateinit var wrappedGlueLineLookup: WrappedGlueLineLookup

    @Autowired
    lateinit var wrappedDateTimeService: WrappedDateTimeService

    @Before
    fun setUp() {
        wrappedGlueLineExecutor.mock = glueLineExecutor
        wrappedGlueLineLookup.mock = glueLineLookup
        wrappedDateTimeService.mock = dateTimeService

    }

    @Test
    fun shouldLoadAllMonitors_whenBooted() {
        assertThat(monitors).hasSize(9);
    }
}
