package org.craftsmenlabs.gareth.validator.services

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.craftsmenlabs.gareth.validator.model.*
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus.*
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
    private lateinit var timeService: TimeService

    val now = LocalDateTime.now()
    val oneSecondAgo = now.minusSeconds(1)
    val twoSecondsAgo = now.minusSeconds(2)
    val nextWeek = now.plusWeeks(1)
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
                result = nextWeek
                minTimes = 0
            }
        }
    }

    @Test
    fun testScheduling() {
        val dto = ExperimentDTO.createDefault(now).copy(id = experimentId)
        val createDTO = service.scheduleNewInstance(dto)!!
        assertThat(createDTO.templateId).isEqualTo(templateId)
        assertThat(createDTO.dueDate).isEqualTo(DateTimeDTO(nextWeek))
        assertThat(createDTO.runContext).isSameAs(dto.runContext)
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
        assertThat(service.getFilteredEntities(projectId = "acme")).hasSize(4)
    }

    @Test
    fun testGetAllFinished() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme", onlyFinished = true).map { it.id }).containsExactlyInAnyOrder("finishedOK", "withError")
    }

    @Test
    fun testGetAllNonFinished() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme", onlyFinished = false).map { it.id }).containsExactlyInAnyOrder("justStarted", "waitingForAssume")
    }

    @Test
    fun testGetAllWithError() {
        setupRetrieval(createExperiments())
        assertThat(service.getFilteredEntities(projectId = "acme", status = ERROR).map { it.id }).containsExactlyInAnyOrder("withError")
    }

    @Test
    fun testGetAllWaitingForBaseline() {
        setupRetrieval(createExperiments())
        assertThat(service.getBaselinesDueForProject("acme").map { it.id }).containsExactlyInAnyOrder("justStarted")
    }

    @Test
    fun testGetAllWaitingForAssume() {
        object : Expectations() {
            init {
                timeService.now()
                result = nextWeek
            }
        }
        setupRetrieval(createExperiments())
        assertThat(service.getAssumesDueForProject("acme").map { it.id }).containsExactlyInAnyOrder("waitingForAssume")
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
                .map { it.id }).containsExactlyInAnyOrder("justStarted", "waitingForAssume")
        assertThat(service.getFilteredEntities(projectId = "acme", createdAfterStr = DateFormatUtils.formatToDateString(now.plusDays(1).toLocalDate()))).isEmpty()
    }

    private fun createExperiments(): List<ExperimentEntity> {
        fun create(id: String,
                   created: LocalDateTime = twoSecondsAgo,
                   assumeDue: LocalDateTime?, completed: LocalDateTime?, status: ExecutionStatus): ExperimentEntity {
            val entity = ExperimentEntity()
            entity.id = id
            entity.name =id
            entity.projectId = "acme"
            entity.baselineDue = oneSecondAgo
            entity.assumeDue = assumeDue
            entity.dateCreated = created
            entity.dateCompleted = completed
            entity.result = status
            entity.assume="A"
            entity.baseline="B"
            entity.timeline="1 days"
            entity.runContext = RunContext()
            return entity
        }

        val finishedOK = create("finishedOK", created = now.minusDays(1), assumeDue = nextWeek, completed = nextWeek, status = SUCCESS)
        val withError = create("withError", created = now.minusDays(1), assumeDue = nextWeek, completed = nextWeek, status = ERROR)
        val justStarted = create("justStarted", assumeDue = null, status = PENDING, completed = null)
        val waitingForAssume = create("waitingForAssume", assumeDue = nextWeek, status = RUNNING, completed = null)
        return listOf<ExperimentEntity>(finishedOK, withError, justStarted, waitingForAssume)
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