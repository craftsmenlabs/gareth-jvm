package org.craftsmenlabs.gareth.jpa

import mockit.Expectations
import mockit.Injectable
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.time.DateTimeService
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDateTime

class JPAExperimentStorageTest {

    @Injectable
    lateinit var dao: ExperimentDao

    val entityconverter = EntityConverter()

    val dateTimeService = DateTimeService()

    lateinit var storage: JPAExperimentStorage

    @Before
    fun setup() {
        storage = JPAExperimentStorage(entityconverter, dao, dateTimeService)
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
    @Ignore
    fun testFiltering() {
        val now = dateTimeService.now()
        val yesterday = now.minusDays(1)
        assertThat(storage.getFiltered()).hasSize(4)
        assertThat(storage.getFiltered(onlyFinished = true)).hasSize(2)
        assertThat(storage.getFiltered(onlyFinished = false)).hasSize(2)
        assertThat(storage.getFiltered(createdAfter = yesterday)).hasSize(4)
        assertThat(storage.getFiltered(createdAfter = now)).hasSize(2)
    }

    private fun createEntity(created: LocalDateTime, completed: LocalDateTime? = null): ExperimentEntity {
        val entity = ExperimentEntity()
        entity.dateCreated = created
        entity.id = 42
        entity.name = "name"
        entity.assume = "assume"
        entity.baseline = "baseline"
        entity.success = "success"
        entity.failure = "failure"
        entity.timeline = "time"
        entity.environment = setOf()
        entity.result = ExecutionStatus.RUNNING
        entity.dateCompleted = completed
        return entity;
    }

}