package org.craftsmenlabs.integration

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.craftsmenlabs.gareth.Application
import org.craftsmenlabs.gareth.integration.TestConfig
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
import org.craftsmenlabs.gareth.model.ExperimentTemplateCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentTemplateUpdateDTO
import org.craftsmenlabs.gareth.model.Gluelines
import org.craftsmenlabs.gareth.time.DateFormatUtils
import org.craftsmenlabs.gareth.time.TimeService
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
@SpringBootTest(classes = arrayOf(Application::class, TestConfig::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(profiles = arrayOf("test", "NOAUTH"))
class ExperimentPersistenceIntegrationTest {

    @Autowired
    lateinit var storage: ExperimentStorage

    @Autowired
    lateinit var timeService: TimeService

    lateinit var today: String
    lateinit var tomorrow: String

    @Before
    fun setup() {
        today = DateFormatUtils.formatToDateString(timeService.midnight())
        tomorrow = DateFormatUtils.formatToDateString(timeService.midnight().plusDays(1))
    }

    @Test
    fun testCreateTemplate() {
        val templateDTO = storage.createTemplate(createDTO())
        val retrieved = storage.getTemplateById(templateDTO.id)
        assertThat(retrieved).isEqualTo(templateDTO)
        assertThat(retrieved.ready).isNotNull()

        //update to an invalid state
        val updated = storage.updateTemplate(ExperimentTemplateUpdateDTO(id = retrieved.id, baseline = "sale of computers"))
        assertThat(updated.glueLines.baseline).isEqualTo("sale of computers")
        assertThat(updated.ready).isNull()

        assertThatThrownBy { storage.createExperiment(templateDTO.id, timeService.now()) }.hasMessage("You cannot start an experiment that is not ready.")

        val corrected = storage.updateTemplate(ExperimentTemplateUpdateDTO(id = retrieved.id, baseline = "sale of fruit"))
        assertThat(corrected.ready).isNotNull()


        val inOneHour = timeService.now().plusHours(1)
        val experiment1 = storage.createExperiment(templateDTO.id, inOneHour)
        assertThat(experiment1.name).isEqualTo("Hello world")

        assertThatThrownBy { storage.updateTemplate(ExperimentTemplateUpdateDTO(id = retrieved.id, name = "Hello world2")) }
                .hasMessage("You cannot update experiment template Hello world. There are already running experiments.")


    }

    private fun createDTO(): ExperimentTemplateCreateDTO {
        return ExperimentTemplateCreateDTO(name = "Hello world",
                glueLines = Gluelines(
                        baseline = "sale of fruit",
                        assume = "sale of fruit has risen by 81 per cent",
                        time = "2 seconds",
                        success = "send email to Sam",
                        failure = "send email to Moos"),
                value = 42)
    }

}