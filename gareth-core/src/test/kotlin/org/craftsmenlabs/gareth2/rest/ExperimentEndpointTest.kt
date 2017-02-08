package org.craftsmenlabs.gareth2.rest

import mockit.Expectations
import mockit.Injectable
import mockit.Verifications
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentDTOConverter
import org.craftsmenlabs.gareth2.time.TimeService
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ExperimentEndpointTest {

    lateinit var endpoint: ExperimentEndpoint
    @Injectable
    lateinit var experimentStorage: ExperimentStorage
    @Injectable
    lateinit var dateTimeService: TimeService

    val experiment = Experiment.createDefault()

    val now = LocalDateTime.now()

    @Before
    fun setUp() {
        endpoint = ExperimentEndpoint(experimentStorage, ExperimentDTOConverter(dateTimeService), dateTimeService)
    }

    @Test
    fun testupsert() {
        object : Expectations() {
            init {
                experimentStorage.save(experiment)
                result = experiment
            }
        }
        val experiment = endpoint.upsert(experimentCreateDTO)
        assertThat(experiment.name).isEqualTo("Test")
    }

    @Test
    fun testGetById() {
        object : Expectations() {
            init {
                experimentStorage.getById(42)
                result = experiment
            }
        }
        val experiment = endpoint.get(42)
        assertThat(experiment).isSameAs(experimentDTO)
    }

    @Test
    fun testGetFilteredByDate() {
        object : Expectations() {
            init {
                dateTimeService.parse_ddMMYYY("10102016")
                result = now

                experimentStorage.getFiltered(now, null)
                result = listOf(experiment)
            }
        }
        val experiments = endpoint.getFiltered("10102016", null)
        assertThat(experiments).hasSize(1)
        assertThat(experiments[0]).isSameAs(experimentDTO)
    }

    @Test
    fun testGetFilteredByCompletedOnly() {
        object : Expectations() {
            init {
                experimentStorage.getFiltered(null, true)
                result = listOf(experiment)
            }
        }
        val experiments = endpoint.getFiltered(null, true)
        assertThat(experiments).hasSize(1)
        assertThat(experiments[0]).isSameAs(experimentDTO)
    }

    @Test
    @Ignore
    fun testStartExperiment() {
        object : Expectations() {
            init {
                dateTimeService.now()
                result = now

                experimentStorage.getById(42)
                result = experiment
            }
        }
        val experiment = endpoint.start(42)
        assertThat(experiment).isSameAs(experimentDTO)
        object : Verifications() {
            init {
                val experiments = mutableListOf<Experiment>()
                experimentStorage.save(withCapture(experiments))
                assertThat(experiments[0].timing.started).isEqualTo(now)
            }
        }
    }

}
