package org.craftsmenlabs.gareth.validator.services

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.craftsmenlabs.gareth.validator.model.DateTimeDTO
import org.craftsmenlabs.gareth.validator.model.ExecutionInterval
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus.*
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.mongo.ExperimentConverter
import org.craftsmenlabs.gareth.validator.mongo.ExperimentDao
import org.craftsmenlabs.gareth.validator.mongo.ExperimentEntity
import org.craftsmenlabs.gareth.validator.mongo.ExperimentTemplateEntity
import org.craftsmenlabs.gareth.validator.time.DateFormatUtils
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ExperimentServiceTest {

    @Injectable
    private lateinit var experimentDao: ExperimentDao
    @Injectable
    private lateinit var templateService: TemplateService
    @Injectable
    private lateinit var converter: ExperimentConverter
    @Injectable
    private lateinit var timeService: TimeService

    val now = LocalDateTime.now()
    val later = now.plusWeeks(1)
    @Tested
    private lateinit var service: ExperimentService

    val templateId = "TPL"
    val experimentId = "EXP"
    @Injectable
    private lateinit var experiment: ExperimentEntity
    @Injectable
    private lateinit var template: ExperimentTemplateEntity

    @Before
    fun setupFetchTemplate() {
        object : Expectations() {
            init {
                timeService.now()
                result = now
                minTimes = 0
                templateService.findByid(templateId)
                result = template
                minTimes = 0
                template.id
                result = templateId
                minTimes = 0
                template.interval
                result = ExecutionInterval.WEEKLY
                minTimes = 0
                experimentDao.findOne("EXP")
                result = experiment
                minTimes = 0
                experiment.templateId
                result = templateId
                minTimes = 0
                timeService.getDelay(now, ExecutionInterval.WEEKLY)
                result = later
                minTimes = 0
            }
        }
    }

    @Test
    fun testScheduling() {
        val dto = ExperimentDTO.createDefault(now).copy(id = experimentId)
        val createDTO = service.scheduleNewInstance(dto)!!
        assertThat(createDTO.templateId).isEqualTo(templateId)
        assertThat(createDTO.dueDate).isEqualTo(DateTimeDTO(later))
        assertThat(createDTO.environment).isSameAs(dto.environment)
    }

    @Test
    fun testSchedulingForNoRepeatTemplate() {
        object : Expectations() {
            init {
                template.interval
                result = ExecutionInterval.NO_REPEAT
            }
        }
        assertThat(service.scheduleNewInstance(ExperimentDTO.createDefault(now).copy(id = experimentId))).isNull()
    }

    @Test
    fun testGetAll() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme")).hasSize(3)
    }

    @Test
    fun testGetAllFinished() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme", onlyFinished = true).map { it.id }).containsExactlyInAnyOrder("finishedOK", "withError")
    }

    @Test
    fun testGetAllNonFinished() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme", onlyFinished = false).map { it.id }).containsExactlyInAnyOrder("justStarted")
    }

    @Test
    fun testGetAllWithError() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme", status = ERROR).map { it.id }).containsExactlyInAnyOrder("withError")
    }

    @Test
    fun testGetAllPending() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme", status = PENDING, onlyFinished = false).map { it.id }).containsExactlyInAnyOrder("justStarted")
    }

    @Test
    fun testGetPendingAndFinishedThrows() {
        assertThatThrownBy { service.getFilteredEntities(projectId = "acme", status = PENDING, onlyFinished = true) }
                .hasMessage("Cannot filter on running status when querying only for finished experiments.")
    }

    @Test
    fun testGetAllCreatedAfterDate() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme", createdAfterStr = DateFormatUtils.formatToDateString(now.toLocalDate()))
                .map { it.id }).containsExactlyInAnyOrder("justStarted")
        assertThat(service.getFilteredEntities(projectId = "acme", createdAfterStr = DateFormatUtils.formatToDateString(now.plusDays(1).toLocalDate()))).isEmpty()
    }

    private fun createExperiments(): List<ExperimentEntity> {
        fun create(id: String, created: LocalDateTime, completed: LocalDateTime?, status: ExecutionStatus): ExperimentEntity {
            val entity = ExperimentEntity()
            entity.id = id
            entity.projectId = "acme"
            entity.dateCreated = created
            entity.dateCompleted = completed
            entity.result = status
            return entity
        }

        val finishedOK = create("finishedOK", now.minusDays(1), completed = later, status = SUCCESS)
        val withError = create("withError", now.minusDays(1), completed = later, status = ERROR)
        val justStarted = create("justStarted", now, null, PENDING)
        return listOf<ExperimentEntity>(finishedOK, withError, justStarted)
    }

    private fun setupRetrieval(dtos: List<ExperimentEntity>) {
        object : Expectations() {
            init {
                experimentDao.findByProjectId("acme")
                result = dtos
            }
        }
    }

}