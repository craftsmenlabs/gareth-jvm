package org.craftsmenlabs.gareth.jpa

import mockit.Expectations
import mockit.Injectable
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.client.GluelineValidatorRestClient
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.time.DateTimeService
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class JPAExperimentStorageTest {

    @Injectable
    lateinit var dao: ExperimentDao

    @Injectable
    lateinit var templateDao: ExperimentTemplateDao

    @Injectable
    lateinit var gluelineLookup: GluelineValidatorRestClient

    val dateTimeService = DateTimeService()

    val entityconverter = EntityConverter(dateTimeService)

    lateinit var storage: JPAExperimentStorage

    @Before
    fun setup() {
        storage = JPAExperimentStorage(entityconverter, dao, gluelineLookup, templateDao, dateTimeService)
        val today = dateTimeService.now().plusMinutes(10)
        val yesterday = dateTimeService.now().minusDays(1)
        val cache = listOf(createEntity(today, null),
                createEntity(today, today),
                createEntity(yesterday, null),
                createEntity(yesterday, yesterday))
        object : Expectations() {
            init {
                dao.findAll()
                result = cache
            }
        }
    }

    @Test
    fun testFiltering() {
        val now = dateTimeService.now()
        val dayBeforeYesterday = now.minusDays(2)
        assertThat(storage.getFiltered()).hasSize(4)
        assertThat(storage.getFiltered(onlyFinished = true)).hasSize(2)
        assertThat(storage.getFiltered(onlyFinished = false)).hasSize(2)
        assertThat(storage.getFiltered(createdAfter = dayBeforeYesterday)).hasSize(4)
        assertThat(storage.getFiltered(createdAfter = now)).hasSize(2)
        assertThat(storage.getFiltered(createdAfter = now, onlyFinished = true)).hasSize(1)
        assertThat(storage.getFiltered(createdAfter = now, onlyFinished = false)).hasSize(1)
    }

    private fun createEntity(created: LocalDateTime, completed: LocalDateTime? = null): ExperimentEntity {
        val entity = ExperimentEntity()
        entity.dateCreated = created
        entity.dateDue = created
        entity.id = 42

        entity.environment = setOf()
        entity.result = ExecutionStatus.PENDING
        entity.dateCompleted = completed
        val template = ExperimentTemplateEntity()
        template.name = "name"
        template.assume = "assume"
        template.baseline = "baseline"
        template.success = "success"
        template.failure = "failure"
        template.timeline = "time"
        entity.template = template
        return entity;
    }

}