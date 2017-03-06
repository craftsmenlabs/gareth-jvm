package org.craftsmenlabs.gareth.rest

import mockit.Expectations
import mockit.Injectable
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.jpa.EntityConverter
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.time.TimeService
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ExperimentEndpointTest {

    lateinit var endpoint: ExperimentEndpoint
    @Injectable
    lateinit var experimentStorage: ExperimentStorage
    @Injectable
    lateinit var converter: EntityConverter
    @Injectable
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var experiment: Experiment
    @Injectable
    lateinit var experimentDTO: ExperimentDTO

    val now = LocalDateTime.now()

    @Before
    fun setUp() {
        endpoint = ExperimentEndpoint(experimentStorage, converter, dateTimeService)
    }

    @Test
    fun testGetById() {
        object : Expectations() {
            init {
                experimentStorage.getById(42)
                result = experiment

                converter.toDTO(experiment)
                result = experimentDTO
            }
        }
        val experiment = endpoint.get(42)
        assertThat(experiment).isSameAs(experimentDTO)
    }

    @Test
    fun testGetFilteredByDate() {
        object : Expectations() {
            init {
                experimentStorage.getFiltered(withInstanceOf(LocalDateTime::class.java), null)
                result = listOf(experiment)

                converter.toDTO(experiment)
                result = experimentDTO
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

                converter.toDTO(experiment)
                result = experimentDTO
            }
        }
        val experiments = endpoint.getFiltered(null, true)
        assertThat(experiments).hasSize(1)
        assertThat(experiments[0]).isSameAs(experimentDTO)
    }

    @Test
    fun testStartExperiment(@Injectable experiment: Experiment) {
        object : Expectations() {
            init {
                experimentStorage.createExperiment(42, now)
                result = experiment

                converter.toDTO(experiment)
                result = experimentDTO
            }
        }
        val experiment = endpoint.start(ExperimentCreateDTO(42, now))
        assertThat(experiment).isSameAs(experimentDTO)
    }

}
