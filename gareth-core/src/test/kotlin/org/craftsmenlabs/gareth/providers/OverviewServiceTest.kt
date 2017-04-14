package org.craftsmenlabs.gareth.providers

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus
import org.craftsmenlabs.gareth.validator.mongo.ExperimentDao
import org.craftsmenlabs.gareth.validator.mongo.ExperimentEntity
import org.craftsmenlabs.gareth.validator.mongo.ExperimentTemplateDao
import org.craftsmenlabs.gareth.validator.mongo.ExperimentTemplateEntity
import org.craftsmenlabs.gareth.validator.services.OverviewService
import org.craftsmenlabs.gareth.validator.time.TimeService
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

    val template = createTemplate("fruit")
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
        assertThat(service.getAllForProject("acme")).isEmpty()
    }

    @Test
    fun testOverviewWithOneTemplateAndNoExperiments() {
        setupDaos(template)
        val overviews = service.getAllForProject("acme")
        assertThat(overviews).hasSize(1)
        assertThat(overviews[0].name).isEqualTo("fruit")
        assertThat(overviews[0].id).isEqualTo("tmp")
    }

    @Test
    fun testWithOneRunningExperiment() {
        val started = createExperiment(template, due = threeDaysAgo, status = ExecutionStatus.RUNNING)
        setupDaos(template, started)
        assertThat(service.getAllForProject("acme")[0].running).isEqualTo(1)
    }

    @Test
    fun testWithOneStartedAndPendingExperiment() {
        val started = createExperiment(template, due = threeDaysAgo)
        setupDaos(template, started)
        assertThat(service.getAllForProject("acme")[0].pending).isEqualTo(1)
    }

    @Test
    fun testWithOneSuccessfulExperiment() {
        val success = createExperiment(template, due = threeDaysAgo, status = ExecutionStatus.SUCCESS, completed = twoDaysAgo)
        setupDaos(template, success)
        assertThat(service.getAllForProject("acme")[0].success).isEqualTo(1)
        assertThat(service.getAllForProject("acme")[0].failed).isEqualTo(0)
        assertThat(service.getAllForProject("acme")[0].running).isEqualTo(0)
    }

    @Test
    fun testWithOneFailedExperiment() {
        val failed = createExperiment(template, due = threeDaysAgo, status = ExecutionStatus.FAILURE, completed = twoDaysAgo)
        setupDaos(template, failed)
        assertThat(service.getAllForProject("acme")[0].success).isEqualTo(0)
        assertThat(service.getAllForProject("acme")[0].failed).isEqualTo(1)
        assertThat(service.getAllForProject("acme")[0].running).isEqualTo(0)
    }

    @Test
    fun testWithOneArchivedTemplate() {
        val vegetables = createTemplate("vegetables")
        val ex1 = createExperiment(template, due = threeDaysAgo)
        val ex2 = createExperiment(template, due = threeDaysAgo)

        vegetables.archived = true
        object : Expectations() {
            init {
                templateDao.findByProjectId("acme")
                result = listOf(template, vegetables)
                experimentDao.findByProjectId("acme")
                result = listOf(ex1, ex2)
            }
        }
        assertThat(service.getAllForProject("acme")).hasSize(1)
    }


    @Test
    fun testWithLastRun() {
        val success = createExperiment(template, due = threeDaysAgo, status = ExecutionStatus.SUCCESS, completed = yesterday)
        val failed = createExperiment(template, due = threeDaysAgo, status = ExecutionStatus.FAILURE, completed = twoDaysAgo)
        setupDaos(template, success, failed)
        assertThat(service.getAllForProject("acme")[0].lastRun).isEqualTo(yesterday)
    }

    @Test
    fun testWithNextRun() {
        val success = createExperiment(template, due = threeDaysAgo, status = ExecutionStatus.SUCCESS, completed = now)
        val failed = createExperiment(template, due = threeDaysAgo, status = ExecutionStatus.FAILURE, completed = tomorrow)
        val startNextWeek = createExperiment(template, due = nextWeek, status = ExecutionStatus.PENDING)
        val startInTwoWeeks = createExperiment(template, due = twoWeeks, status = ExecutionStatus.PENDING)
        setupDaos(template, success, failed, startNextWeek, startInTwoWeeks)
        assertThat(service.getAllForProject("acme")[0].nextRun).isEqualTo(nextWeek)
    }


    private fun setupDaos(template: ExperimentTemplateEntity,
                          vararg experiments: ExperimentEntity) {
        object : Expectations() {
            init {
                templateDao.findByProjectId("acme")
                result = template
                experimentDao.findByProjectId("acme")
                result = experiments
            }
        }
    }

    private fun createTemplate(name: String): ExperimentTemplateEntity {
        val template = ExperimentTemplateEntity()
        template.name = name
        template.id = "tmp"
        template.projectId = "acme"
        //the following are not part of the test, but cannot be null
        template.assume = "assume"
        template.baseline = "baseline"
        template.success = "success"
        template.failure = "failure"
        template.timeline = "time"
        template.dateCreated = LocalDateTime.now()
        template.archived = false
        return template
    }

    private fun createExperiment(template: ExperimentTemplateEntity,
                                 due: LocalDateTime,
                                 completed: LocalDateTime? = null,
                                 status: ExecutionStatus = ExecutionStatus.PENDING

    ): ExperimentEntity {
        val exp = ExperimentEntity(null)
        exp.templateId = template.id!!
        exp.assume = template.assume!!
        exp.baseline = template.baseline!!
        exp.success = template.success
        exp.failure = template.failure
        exp.timeline = template.timeline!!
        exp.name = template.name
        exp.dateCreated = threeDaysAgo
        exp.dateDue = due
        exp.dateCompleted = completed
        exp.result = status
        exp.archived = false
        return exp
    }
}