package org.craftsmenlabs.gareth.providers

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.jpa.ExperimentDao
import org.craftsmenlabs.gareth.jpa.ExperimentEntity
import org.craftsmenlabs.gareth.jpa.ExperimentTemplateDao
import org.craftsmenlabs.gareth.jpa.ExperimentTemplateEntity
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.time.TimeService
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class OverviewServiceTest {


    @Injectable
    lateinit var templateDao: ExperimentTemplateDao
    @Injectable
    lateinit var experimentDao: ExperimentDao
    @Injectable
    lateinit var timeService: TimeService
    @Tested
    lateinit var service: OverviewService

    val now = LocalDateTime.now()
    val threeDaysAgo = now.minusDays(3)
    val twoDaysAgo = now.minusDays(2)
    val yesterday = now.minusDays(1)
    val tomorrow = now.plusDays(1)
    val nextWeek = now.plusDays(7)
    val twoWeeks = now.plusDays(14)

    val template = createTemplate(42, "fruit")
    @Before
    fun setup() {
        object : Expectations() {
            init {
                timeService.now()
                result = now
                minTimes = 0
            }
        }
    }

    @Test
    fun testOverviewWithNoExperimentTemplates() {
        assertThat(service.getAll()).isEmpty()
    }

    @Test
    fun testOverviewWithOneTemplateAndNoExperiments() {
        setupTemplateDao(template)
        val overviews = service.getAll()
        assertThat(overviews).hasSize(1)
        assertThat(overviews[0].name).isEqualTo("fruit")
        assertThat(overviews[0].templateId).isEqualTo(42)
    }

    @Test
    fun testWithOneRunningExperiment() {
        setupTemplateDao(template)
        val started = createExperiment(template, 1, started = threeDaysAgo, status = ExecutionStatus.RUNNING)
        setupExperimentDaoForTemplate(template, started)
        assertThat(service.getAll()[0].running).isEqualTo(1)
    }

    @Test
    fun testWithOneStartedAndPendingExperiment() {
        setupTemplateDao(template)
        val started = createExperiment(template, 1, started = threeDaysAgo)
        setupExperimentDaoForTemplate(template, started)
        assertThat(service.getAll()[0].pending).isEqualTo(1)
    }

    @Test
    fun testWithNotReadyExperiment() {
        setupTemplateDao(template)
        val notReady = createExperiment(template, 1, ready = null)
        setupExperimentDaoForTemplate(template, notReady)
        assertThat(service.getAll()[0].pending).isEqualTo(0)
    }

    @Test
    fun testWithOneSuccessfulExperiment() {
        setupTemplateDao(template)
        val success = createExperiment(template, 1, started = threeDaysAgo, status = ExecutionStatus.SUCCESS, completed = twoDaysAgo)
        setupExperimentDaoForTemplate(template, success)
        assertThat(service.getAll()[0].success).isEqualTo(1)
        assertThat(service.getAll()[0].failed).isEqualTo(0)
        assertThat(service.getAll()[0].running).isEqualTo(0)
    }

    @Test
    fun testWithOneFailedExperiment() {
        setupTemplateDao(template)
        val failed = createExperiment(template, 1, started = threeDaysAgo, status = ExecutionStatus.FAILURE, completed = twoDaysAgo)
        setupExperimentDaoForTemplate(template, failed)
        assertThat(service.getAll()[0].success).isEqualTo(0)
        assertThat(service.getAll()[0].failed).isEqualTo(1)
        assertThat(service.getAll()[0].running).isEqualTo(0)
    }


    @Test
    fun testWithLastRun() {
        setupTemplateDao(template)
        val success = createExperiment(template, 1, started = threeDaysAgo, status = ExecutionStatus.SUCCESS, completed = yesterday)
        val failed = createExperiment(template, 2, started = threeDaysAgo, status = ExecutionStatus.FAILURE, completed = twoDaysAgo)
        setupExperimentDaoForTemplate(template, success, failed)
        assertThat(service.getAll()[0].lastRun).isEqualTo(yesterday)
    }

    @Test
    fun testWithNextRun() {
        setupTemplateDao(template)
        val success = createExperiment(template, 1, started = threeDaysAgo, status = ExecutionStatus.SUCCESS, completed = now)
        val failed = createExperiment(template, 2, started = threeDaysAgo, status = ExecutionStatus.FAILURE, completed = tomorrow)
        val startNextWeek = createExperiment(template, 2, started = nextWeek, status = ExecutionStatus.PENDING)
        val startInTwoWeeks = createExperiment(template, 2, started = twoWeeks, status = ExecutionStatus.PENDING)
        setupExperimentDaoForTemplate(template, success, failed, startNextWeek, startInTwoWeeks)
        assertThat(service.getAll()[0].nextRun).isEqualTo(nextWeek)
    }

    private fun setupTemplateDao(vararg templates: ExperimentTemplateEntity) {
        object : Expectations() {
            init {
                templateDao.findAll()
                result = templates.toList()
            }
        }
    }

    private fun setupExperimentDaoForTemplate(template: ExperimentTemplateEntity, vararg experiments: ExperimentEntity) {
        object : Expectations() {
            init {
                experimentDao.findByTemplate(template)
                result = experiments
            }
        }
    }

    private fun createTemplate(id: Long, name: String): ExperimentTemplateEntity {
        val template = ExperimentTemplateEntity()
        template.id = id
        template.name = name
        //the following are not part of the test, but cannot be null
        template.assume = "assume"
        template.baseline = "baseline"
        template.success = "success"
        template.failure = "failure"
        template.dateCreated = LocalDateTime.now()
        return template
    }

    private fun createExperiment(template: ExperimentTemplateEntity,
                                 id: Long,
                                 ready: LocalDateTime? = threeDaysAgo,
                                 started: LocalDateTime? = null,
                                 completed: LocalDateTime? = null,
                                 status: ExecutionStatus = ExecutionStatus.PENDING

    ): ExperimentEntity {
        val exp = ExperimentEntity(id)
        exp.template = template
        exp.dateCreated = threeDaysAgo
        exp.dateReady = ready
        exp.dateStarted = started
        exp.dateCompleted = completed
        exp.result = status
        return exp
    }
}