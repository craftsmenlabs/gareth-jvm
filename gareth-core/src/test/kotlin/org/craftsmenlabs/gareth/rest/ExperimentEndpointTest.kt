package org.craftsmenlabs.gareth.rest

import mockit.Expectations
import mockit.Injectable
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.model.ExperimentDTOConverter
import org.craftsmenlabs.gareth.time.TimeService
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ExperimentEndpointTest {

    lateinit var endpoint: ExperimentEndpoint
    @Injectable
    lateinit var experimentStorage: ExperimentStorage
    @Injectable
    lateinit var converter: ExperimentDTOConverter
    @Injectable
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var experiment: Experiment
    @Injectable
    lateinit var experimentCreateDTO: ExperimentCreateDTO
    @Injectable
    lateinit var experimentDTO: ExperimentDTO

    val now = LocalDateTime.now()

    @Before
    fun setUp() {
        endpoint = ExperimentEndpoint(experimentStorage, converter, dateTimeService)
    }

    @Test
    fun testupsert() {
        object : Expectations() {
            init {
                converter.createExperiment(experimentCreateDTO)
                result = experiment

                experimentStorage.save(experiment)
                result = experiment

                converter.createDTO(experiment)
                result = experimentDTO
            }
        }
        val experiment = endpoint.upsert(experimentCreateDTO)
        assertThat(experiment).isSameAs(experimentDTO)
    }

    @Test
    fun testGetById() {
        object : Expectations() {
            init {
                experimentStorage.getById(42)
                result = experiment

                converter.createDTO(experiment)
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

                converter.createDTO(experiment)
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

                converter.createDTO(experiment)
                result = experimentDTO
            }
        }
        val experiments = endpoint.getFiltered(null, true)
        assertThat(experiments).hasSize(1)
        assertThat(experiments[0]).isSameAs(experimentDTO)
    }

    @Test
    fun testStartExperiment() {
        val experimentTemplate = Experiment.createDefault()
        object : Expectations() {
            init {
                dateTimeService.now()
                result = now

                experimentStorage.getById(42)
                result = experimentTemplate

                converter.createDTO(withAny(experimentTemplate))
            }
        }
        val experiment = endpoint.start(42)
        assertThat(experiment).isSameAs(experimentDTO)
       /* object : Verifications() {
            init {
                val experiments = mutableListOf<Experiment>()
                experimentStorage.save(withCapture(experiments))
                assertThat(experiments[0].timing.started).isEqualTo(now)
            }
        }*/
    }

}
